package de.dude.timetracker

import de.dude.library.javafx.CleanStage
import de.dude.library.javafx.CleanStageApp
import de.dude.library.javafx.LayoutHelper
import de.dude.timetracker.controller.MainController
import de.dude.timetracker.repository.Defaults
import javafx.scene.Scene

class App : CleanStageApp() {

    override fun onStageCreated(stage: CleanStage) {
        stage.apply {
            isHideOnFocusLoss = true
            isAlwaysOnTop = true
            minWidth = Defaults.STAGE_MIN_WIDTH
            minHeight = Defaults.STAGE_MIN_HEIGHT
            scene = Scene(LayoutHelper.load("navigation").view)
            enablePersistPosition("TIME_TRACKER_WINDOW")
            enableResizeable(8)
        }
        MainController.init(stage)
    }

}