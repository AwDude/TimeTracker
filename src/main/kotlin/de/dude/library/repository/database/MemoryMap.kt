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

    fun putBool(address: Long, bool: Boolean) = putByte(address, if (bool) 1 else 0)
    fun getBool(address: Long) = getByte(address) == 1.toByte()

    fun putByte(address: Long, byte: Byte) = unsafe.putByte(null, address.checkSize(1), byte)
    fun getByte(address: Long) = unsafe.getByte(null, address.checkSize(1))

    fun putChar(address: Long, char: Char) = unsafe.putCharUnaligned(null, address.checkSize(2), char)
    fun getChar(address: Long) = unsafe.getCharUnaligned(null, address.checkSize(2))

    fun putShort(address: Long, short: Short) = unsafe.putShortUnaligned(null, address.checkSize(2), short)
    fun getShort(address: Long) = unsafe.getShortUnaligned(null, address.checkSize(2))

    fun putInt(address: Long, int: Int) = unsafe.putIntUnaligned(null, address.checkSize(4), int)
    fun getInt(address: Long) = unsafe.getIntUnaligned(null, address.checkSize(4))

    fun putFloat(address: Long, float: Float) = putInt(address, float.toRawBits())
    fun getFloat(address: Long) = Float.fromBits(getInt(address))

    fun putLong(address: Long, long: Long) = unsafe.putLongUnaligned(null, address.checkSize(8), long)
    fun getLong(address: Long) = unsafe.getLongUnaligned(null, address.checkSize(8))

    fun putDouble(address: Long, double: Double) = putLong(address, double.toRawBits())
    fun getDouble(address: Long) = Double.fromBits(getLong(address))

    // TODO put/get ByteArray

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
        unsafe.copyMemory(from.checkSize(numBytes), to.checkSize(numBytes), numBytes)
    }

    private fun Long.checkSize(valueSize: Long) = this.absolute.also { address ->
        val targetSize = address + valueSize
        if (targetSize > size) {
            size = targetSize
            if (targetSize > capacity) {
                mapMemory(targetSize + bufferSize)
            }
        }
    }

    private val Long.absolute: Long
        get() {
            if (this < 0) throw IllegalArgumentException("Provided relative address is negative")
            return (baseAddress + this).also { if (it < 0) throw IllegalArgumentException("Address overflow") }
        }

    private fun unmap() = unmapper?.let {
        // Reflects.unmapper_unmap.invoke(it)
        unmapper = null
        // TODO
        // channel_unmapMemory
        // MappedMemoryUtils.force
        // MappedMemoryUtils.unload
        Reflects.channel_unmapMemory.invoke(channel, baseAddress, capacity)
    }

    override fun close() {
        unmap()
        channel.truncate(size)
        channel.close()
    }

}

private object Reflects {

    val channel_mapMemory: Method
    val channel_unmapMemory: Method
    val unmapper_unmap: Method
    val unmapper_address: Field
    val unmapper_pagePosition: Field

    init {
        val channelClass = Class.forName("sun.nio.ch.FileChannelImpl")
        channel_mapMemory = channelClass.getDeclaredMethod(
            "mapInternal", FileChannel.MapMode::class.java, Long::class.java, Long::class.java
        ).apply { isAccessible = true }
        channel_unmapMemory = channelClass.getDeclaredMethod(
            "unmap0", Long::class.java, Long::class.java
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