package de.dude.timetracker

import de.dude.library.javafx.CleanApp
import de.dude.library.javafx.view.CleanStage
import de.dude.timetracker.controller.MainController
import de.dude.timetracker.repository.Defaults
import javafx.application.Platform
import java.nio.channels.FileChannel
import java.nio.channels.FileLock
import java.nio.file.Path
import java.nio.file.StandardOpenOption
import kotlin.concurrent.thread
import kotlin.system.measureNanoTime

class App : CleanApp() {

    override fun onStageCreated(stage: CleanStage) {

        val channel: FileChannel = FileChannel.open(Path.of("docs/ToDo.txt"), StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.READ)

        thread(true) {
            val lock = channel.readLock(1)
            Thread.sleep(1000)
            lock?.release()
        }

        channel.map(FileChannel.MapMode.READ_WRITE, 123123, 12)

        Platform.exit()
        return
        stage.apply {
            isHideOnFocusLoss = true
            isAlwaysOnTop = true
            isDraggable = true
            minWidth = Defaults.STAGE_MIN_WIDTH
            minHeight = Defaults.STAGE_MIN_HEIGHT
            enablePersistPosition("TIME_TRACKER_WINDOW")
            enableResizeable(6)
        }
        MainController(stage)
    }

    fun FileChannel.readLock(i: Int): FileLock? {
        var lock: FileLock? = null
        var exc: Exception? = null
        val time = measureNanoTime {
            try {
                lock = tryLock(0, Long.MAX_VALUE, true)
            } catch (e: Exception) {
                exc = e
            }
        }
        println("Try lock $i. Exists: ${lock != null}. Valid: ${lock?.isValid ?: false}. Shared: ${lock?.isShared ?: false} Time: $time. Error: $exc.")
        return lock
    }

}