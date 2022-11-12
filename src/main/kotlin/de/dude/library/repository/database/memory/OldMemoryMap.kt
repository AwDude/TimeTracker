package de.dude.library.repository.database.memory

import de.dude.library.util.Mighty
import java.io.Closeable
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel
import java.nio.file.Path
import java.nio.file.StandardOpenOption.*


private const val ALLOC_SIZE = 1024L * 1024L

class OldMemoryMap(fileName: String) : Closeable {

    private val channel = FileChannel.open(Path.of(fileName), CREATE, WRITE, READ)
    private val buffers = mutableListOf<MappedByteBuffer>()
    private var size = channel.size()

    init {
        checkCapacity(size + ALLOC_SIZE)
    }

    private fun <T> translate(address: Long, action: MappedByteBuffer.(index: Int) -> T): T {
        val bufferNumber = (address / Int.MAX_VALUE).toInt()
        val bufferIndex = (address % Int.MAX_VALUE).toInt()
        return buffers[bufferNumber].action(bufferIndex)
    }

    fun test() {
        val bytes = ByteArray(4)
        String(bytes)
        "asd".toByteArray()
    }

    fun readString(address: Long, length: Int): String {
        translate(address) { index -> }

        val bytes = ByteArray(4) // TODO
        return ""
    }

/*    fun writeString(): String {

    }*/

    private fun checkCapacity(targetCapacity: Long) {
        val lastBuffer = buffers.last()
        var lastBufferStartAddress = (buffers.size - 1L) * Int.MAX_VALUE
        val lastBufferCapacity = lastBuffer.capacity()
        val currentCapacity = lastBufferStartAddress + lastBufferCapacity
        var lackingCapacity = targetCapacity - currentCapacity

        if (lackingCapacity > 0) {
            lackingCapacity += ALLOC_SIZE
            val lastBufferFreeCapacity = Int.MAX_VALUE - lastBufferCapacity
            removeLastBuffer()

            // Required additional capacity fits in one buffer.
            if (lastBufferFreeCapacity >= lackingCapacity) {
                val newCapacity = lastBufferCapacity + lackingCapacity
                addBuffer(lastBufferStartAddress, newCapacity)

                // Additional buffers have to get instantiated.
            } else {
                // Last (already removed) buffer can be replaced by a max capacity buffer,
                // which behaves like filling it up to its max capacity.
                // Therefore, add a max capacity buffer and adjust the lacking capacity.
                lackingCapacity -= lastBufferFreeCapacity
                val additionalMaxCapacityBuffers = (lackingCapacity / Int.MAX_VALUE) + 1

                for (i in 0 until additionalMaxCapacityBuffers) {
                    addBuffer(lastBufferStartAddress, Int.MAX_VALUE.toLong())
                    lastBufferStartAddress += Int.MAX_VALUE
                }

                val remainingLackingCapacity = lackingCapacity % Int.MAX_VALUE
                if (remainingLackingCapacity > 0) {
                    addBuffer(lastBufferStartAddress, remainingLackingCapacity)
                }
            }
        }
    }

    private fun addBuffer(address: Long, size: Long) =
        buffers.add(channel.map(FileChannel.MapMode.READ_WRITE, address, size))

    private fun removeLastBuffer() = Mighty.unsafe.invokeCleaner(buffers.removeLast().apply { force() })

    override fun close() {
        repeat(buffers.size) { removeLastBuffer() }

        channel.truncate(128)
        channel.close()
    }
/*
    private fun createDummyBuffer() = object : MappedByteBuffer() {

    }*/

}