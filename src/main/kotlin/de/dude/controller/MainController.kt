package de.dude.controller

import de.dude.controller.`interface`.Exitable
import de.dude.repository.Defaults
import de.dude.repository.Settings
import de.dude.util.ResizeHelper
import de.dude.util.TrayService
import de.dude.util.getTaskBarHeight
import de.dude.util.isInScreen
import de.dude.view.Dialog
import javafx.application.Platform
import javafx.fxml.FXMLLoader
import javafx.geometry.Point2D
import javafx.scene.Scene
import javafx.stage.Screen
import javafx.stage.Stage
import java.util.*
import kotlin.concurrent.scheduleAtFixedRate

private const val MIN_IN_MS = 100L//60000L

class MainController(private val stage: Stage) : Exitable, TrayService.ActionReceiver,
    NavigationController.ActionReceiver {

    private val trayService = TrayService(this)
    private var navigationController: Exitable? = null

    @Volatile
    private var totalMinutes = 0
    private var timer: Timer? = null

    init {
        initStage()
    }

    private fun initStage() {
        val loader = FXMLLoader(javaClass.getResource("/layouts/navigation.fxml"))
        stage.apply {
            minHeight = Defaults.STAGE_MIN_HEIGHT
            minWidth = Defaults.STAGE_MIN_WIDTH
            val width = Settings.stageWidth
            val height = Settings.stageHeight
            val point = getBestStagePosition(Settings.stageX, Settings.stageY, width, height)
            x = point.x
            y = point.y
            scene = Scene(loader.load(), width, height)
            focusedProperty().addListener { _, _, isFocused -> if (!isFocused) stage.hide() }
        }
        navigationController = loader.getController<NavigationController>().apply {
            actionReceiver = this@MainController
        }
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

    private fun getBestStagePosition(x: Double?, y: Double?, width: Double, height: Double): Point2D {
        if (x != null && y != null && isInScreen(x, y, width, height)) return Point2D(x, y)
        val screenBounds = Screen.getPrimary().bounds
        val newX = screenBounds.maxX - width
        val newY = screenBounds.maxY - height - getTaskBarHeight() - 1
        return Point2D(newX, newY)
    }

    override fun exit() {
        if (timer == null || Dialog.confirmExit()) Platform.exit()
    }

    override fun onExit() {
        stage.hide()
        trayService.onExit()
        // TODO Save new times to file
        navigationController?.onExit()
        navigationController = null
    }

}