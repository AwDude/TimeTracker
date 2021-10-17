package de.dude.library.repository.database

import de.dude.library.util.Mighty
import java.io.RandomAccessFile
import java.nio.channels.FileChannel

private const val SIZE = 10
private const val FILE_NAME = "test.txt"

class MMF {

    private var time = System.currentTimeMillis()

    private val buffer = mapMemory(0, SIZE.toLong()).also { log("finished mapping") }
    private val address = getBufferAddress().also { log("read address: $it") }

    init {
        test()
    }

    private fun test() {
        val text = "123 Test".encodeToByteArray()
        log("text bytes = ${text.size}")


        buffer.put(SIZE - text.size, text)
        log("wrote bytes")
        move(text.size, SIZE - text.size, 0)
        log("moved bytes")
        move(text.size, 0, SIZE - text.size)
        log("moved many bytes")

        val buffer2 = mapMemory(SIZE.toLong(), 20)
        buffer2.put(SIZE - text.size, text)
    }

    private fun log(message: String) {
        val time = System.currentTimeMillis()
        val delay = time - this.time
        println("$delay ms | $message")
        this.time = time
    }

    private fun move(numBytes: Int, from: Int, to: Int) {
        Mighty.unsafe.copyMemory(toAddress(from), toAddress(to), numBytes.toLong())
    }

    private fun toAddress(position: Int) = address + position

    private fun mapMemory(position: Long, size: Long) = RandomAccessFile(FILE_NAME, "rw").use {
        it.channel.map(FileChannel.MapMode.READ_WRITE, position, size)
    }

    private fun getBufferAddress() = Mighty.ofObject(buffer).get<Long>("address")


    private fun test2() {
        val mightyBuffer = Mighty.ofObject(buffer)
        val absoluteBufferAddress = mightyBuffer.get<Long>("address")

        // val charAddress = absoluteBufferAddress + i
        // putChar(charAddress, x)
        buffer::class.java.getDeclaredMethod("putChar", Long::class.java, Char::class.java)
    }

}