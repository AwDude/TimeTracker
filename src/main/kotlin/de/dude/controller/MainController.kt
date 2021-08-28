package de.dude.controller

import de.dude.action.ActionBus
import de.dude.action.actions.AppAction
import de.dude.action.actions.LifeCycleAction
import de.dude.action.actions.TrayAction
import de.dude.util.ResizeHelper
import de.dude.util.TrayService
import de.dude.view.Dialog
import javafx.application.Platform
import javafx.fxml.FXMLLoader
import javafx.scene.Scene
import javafx.stage.Stage
import java.util.*
import kotlin.concurrent.scheduleAtFixedRate

private const val MIN_IN_MS = 100L//60000L

class MainController(private val stage: Stage) : LifeCycleAction, TrayAction, AppAction {

    private val trayService = TrayService()

    @Volatile
    private var totalMinutes = 0
    private var timer: Timer? = null

    init {
        ActionBus.hook(this)
        initStage()
    }

    private fun initStage() {
        val loader = FXMLLoader(javaClass.getResource("/layouts/navigation.fxml"))
        stage.scene = Scene(loader.load())
        ResizeHelper(stage, 4)
    }

    private fun showStage() {
        Platform.runLater(stage::show)
    }

    override fun onClick() {
        if (timer == null) {
            startTimer()
        } else {
            stopTimer()
            showStage()
        }
    }

    override fun onRightClick() {
        showStage()
    }

    private fun startTimer() {
        totalMinutes = -1
        timer = Timer(true).apply {
            scheduleAtFixedRate(0, MIN_IN_MS) { updateTime() }
        }
    }

    private fun stopTimer() = timer?.apply {
        cancel()
        purge()
        timer = null
    }.also { trayService.showIdle() }

    private fun updateTime() {
        totalMinutes++
        val hours = if (totalMinutes > 0) totalMinutes / 60 else 0
        val minutes = totalMinutes % 60
        updateIconTime(hours, minutes)
    }

    private fun updateIconTime(hours: Int, minutes: Int) {
        var hourText: String? = null
        val minuteText = if (hours > 0) {
            hourText = hours.toString()
            String.format("%02d", totalMinutes % 60)
        } else {
            String.format("%2s", minutes)
        }
        trayService.showTime(minuteText, hourText)
    }

    override fun exit() {
        if (timer == null || Dialog.confirmExit()) Platform.exit()
    }

    override fun onExit() {
        stage.hide()
        // TODO Save new times to file
    }

}