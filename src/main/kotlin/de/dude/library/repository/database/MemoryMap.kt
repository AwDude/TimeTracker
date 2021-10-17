@file:Suppress("JAVA_MODULE_DOES_NOT_EXPORT_PACKAGE")

package de.dude.library.repository.database

import de.dude.library.util.getAs
import jdk.internal.misc.Unsafe
import java.lang.reflect.Field
import java.lang.reflect.Method
import java.nio.channels.FileChannel
import java.nio.channels.FileChannel.MapMode.READ_WRITE
import java.nio.file.Path
import java.nio.file.StandardOpenOption.*

private const val DEFAULT_BUFFER_SIZE = 1024L * 1024L

internal class MemoryMap(fileName: String, private val bufferSize: Long = DEFAULT_BUFFER_SIZE) : AutoCloseable {

    private val unsafe = Reflects.getUnsafe()
    private val channel = FileChannel.open(Path.of(fileName), CREATE, WRITE, READ)
    private var size = channel.size()
    private var capacity = 0L
    private var baseAddress = -1L
    private var unmapper: Any? = null

    init {
        mapMemory(size + bufferSize)
    }

    fun putChar(address: Long, char: Char) =
        unsafe.putCharUnaligned(null, address.absolute.checkRange(Char.numBytes), char)

    private fun mapMemory(capacity: Long) {
        unmap()
        if (capacity <= 0) throw IllegalArgumentException("Invalid capacity: $capacity")
        this.capacity = capacity
        unmapper = Reflects.channel_mapMemory.invoke(channel, READ_WRITE, 0L, capacity)
        val address = Reflects.unmapper_address.getAs<Long>(unmapper)
        val pagePosition = Reflects.unmapper_pagePosition.getAs<Int>(unmapper)
        baseAddress = address + pagePosition
    }

    private fun move(numBytes: Long, from: Long, to: Long) {
        unsafe.copyMemory(from.absolute, to.absolute, numBytes)
    }

    private val Long.absolute get() = baseAddress + this

    private fun Long.checkRange(size: Byte) = this.also { address ->
        if (address < 0 || address + size >= baseAddress + capacity) throw IllegalArgumentException("Invalid address or size led")
    }

    private fun unmap() = unmapper?.let { Reflects.unmapper_unmap.invoke(it) }

    override fun close() {
        unmap()
        channel.truncate(size)
        channel.close()
    }
}

private object Reflects {
    val channel_mapMemory: Method
    val unmapper_unmap: Method
    val unmapper_address: Field
    val unmapper_pagePosition: Field

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

    fun getUnsafe(): Unsafe {
        val outerUnsafeClass = sun.misc.Unsafe::class.java
        val outerUnsafeField = outerUnsafeClass.getDeclaredField("theUnsafe").apply { isAccessible = true }
        val outerUnsafe = outerUnsafeField[null] as sun.misc.Unsafe
        val unsafeField = Unsafe::class.java.getDeclaredField("theUnsafe")
        outerUnsafe.apply {
            val unsafeBase = staticFieldBase(unsafeField)
            val unsafeOffset = staticFieldOffset(unsafeField)
            return getObject(unsafeBase, unsafeOffset) as Unsafe
        }
    }
}

internal val Boolean.Companion.numBytes: Byte get() = 1
internal val Byte.Companion.numBytes: Byte get() = 1
internal val Char.Companion.numBytes: Byte get() = 2
internal val Short.Companion.numBytes: Byte get() = 2
internal val Int.Companion.numBytes: Byte get() = 4
internal val Float.Companion.numBytes: Byte get() = 4
internal val Long.Companion.numBytes: Byte get() = 8
internal val Double.Companion.numBytes: Byte get() = 8