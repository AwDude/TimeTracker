package de.dude.library.repository.database

import de.dude.library.util.getAs
import java.lang.reflect.Field
import java.lang.reflect.Method
import java.nio.channels.FileChannel
import java.nio.channels.FileChannel.MapMode.READ_WRITE


internal class MemoryMapping(fileChannel: FileChannel, capacity: Long) {

    internal companion object {
        private val channel_mapMemory: Method
        private val unmapper_unmap: Method
        private val unmapper_address: Field
        private val unmapper_pagePosition: Field

        init {
            val channelClass = Class.forName("sun.nio.ch.FileChannelImpl")
            channel_mapMemory = channelClass.getDeclaredMethod(
                "mapInternal", FileChannel.MapMode::class.java, Long::class.java, Long::class.java
            ).apply { isAccessible = true }

            val unmapperClass = Class.forName("sun.nio.ch.FileChannelImpl\$Unmapper")
            unmapper_address = unmapperClass.getDeclaredField("address").apply { isAccessible = true }
            unmapper_pagePosition = unmapperClass.getDeclaredField("pagePosition").apply { isAccessible = true }
            unmapper_unmap = unmapperClass.getDeclaredMethod("unmap").apply { isAccessible = true }
        }
    }

    private val unmapper: Any
    internal val baseAddress: Long
        get() = unmapper_address.getAs<Long>(unmapper) + unmapper_pagePosition.getAs<Int>(unmapper)

    init {
        if (capacity <= 0) throw IllegalArgumentException("Invalid capacity: $capacity")
        unmapper = channel_mapMemory.invoke(fileChannel, READ_WRITE, 0L, capacity)
    }

    internal fun flush() {
        // TODO
    }

    internal fun unmap() = unmapper_unmap.invoke(unmapper)

}