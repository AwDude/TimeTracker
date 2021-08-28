package de.dude.library.javafx

import de.dude.timetracker.repository.Defaults
import de.dude.timetracker.repository.Settings
import javafx.geometry.Point2D
import javafx.stage.Screen
import javafx.stage.Stage
import javafx.stage.StageStyle

class CleanStage(parentStage: Stage? = null) : Stage() {

    init {
        initStyle(StageStyle.UNDECORATED)
        val targetWidth = Settings.stageWidth
        val targetHeight = Settings.stageHeight
        val bestPos = getBestStagePosition(targetWidth, targetHeight)
        x = bestPos.x
        y = bestPos.y
        width = targetWidth
        height = targetHeight
        minWidth = Defaults.STAGE_MIN_WIDTH
        minHeight = Defaults.STAGE_MIN_HEIGHT
        isAlwaysOnTop = true
        focusedProperty().addListener { _, _, isFocused -> if (!isFocused) hide() }
        parentStage?.let { initOwner(it) }
    }

    private fun getBestStagePosition(targetWidth: Double, targetHeight: Double): Point2D {
        val targetX = Settings.stageX
        val targetY = Settings.stageY

        if (targetX != null && targetY != null && isInScreen(targetX, targetY, targetWidth, targetHeight)) {
            return Point2D(targetX, targetY)
        }
        val screenBounds = Screen.getPrimary().bounds
        val newX = screenBounds.maxX - targetWidth
        val newY = screenBounds.maxY - targetHeight - getTaskBarHeight() - 1
        return Point2D(newX, newY)
    }

}