package de.dude.library.repository.database.entity

import de.dude.library.util.Mighty
import java.io.Closeable
import java.nio.channels.FileChannel
import java.nio.file.Path
import java.nio.file.StandardOpenOption.*

private const val ENTITY_FILE_EXTENSION = ".entity"

class EntityMap(entityName: String, private val entitySize: Int) : Closeable {

    private val channel = FileChannel.open(Path.of(entityName + ENTITY_FILE_EXTENSION), CREATE, WRITE, READ)

    private val buffer = channel.map(FileChannel.MapMode.READ_WRITE, 0, 1024)
    private val buffer2 = channel.map(FileChannel.MapMode.READ_WRITE, 1024, 1024)

    init {
    }

    override fun close() {
        // TODO force write of all buffers
        Mighty.unsafe.invokeCleaner(buffer)
        Mighty.unsafe.invokeCleaner(buffer2)
        channel.truncate(128)
        channel.close()
    }
}