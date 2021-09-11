package de.dude.timetracker.controller

import de.dude.library.action.ActionBus
import de.dude.timetracker.TrayService
import de.dude.timetracker.action.AppAction
import de.dude.timetracker.action.LifeCycleAction
import de.dude.timetracker.action.TrayAction
import de.dude.timetracker.view.Dialog
import javafx.application.Platform
import javafx.stage.Stage
import java.util.*
import kotlin.concurrent.scheduleAtFixedRate

private const val MIN_IN_MS = 100L//60000L

object MainController : LifeCycleAction, TrayAction, AppAction {

    private val trayService = TrayService()
    private lateinit var stage: Stage

    @Volatile
    private var totalMinutes = 0
    private var timer: Timer? = null

    fun init(stage: Stage) {
        this.stage = stage
        ActionBus.hook(this)
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