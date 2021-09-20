package de.dude.timetracker.controller

import de.dude.library.action.ActionBus
import de.dude.library.javafx.util.runOnUI
import de.dude.timetracker.action.TimerAction
import de.dude.timetracker.service.TimerService
import javafx.fxml.FXML
import javafx.scene.control.Button

class TimesController : ViewController(), TimerAction {

    @FXML
    private lateinit var startButton: Button

    @FXML
    private lateinit var stopButton: Button

    override fun onCreate() {
        ActionBus.hook(this)
        setRunning(TimerService.isRunning)
    }

    @FXML
    private fun start() {
        TimerService.start()
    }

    @FXML
    private fun stop() {
        TimerService.stop()
    }

    override fun onStart() = setRunning(true)

    override fun onStop() = setRunning(false)

    private fun setRunning(isRunning: Boolean) = runOnUI {
        startButton.isVisible = !isRunning
        startButton.isManaged = !isRunning
        stopButton.isVisible = isRunning
        stopButton.isManaged = isRunning
    }

    override fun onUpdate(hours: Int, minutes: Int) = runOnUI {
        stopButton.text = String.format("%3s:%02d", hours, minutes)
        //stopButton.text = if (hours > 0) String.format("$hours:%02d", minutes % 60) else minutes.toString()
    }
}