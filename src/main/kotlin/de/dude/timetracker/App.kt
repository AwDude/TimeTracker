package de.dude.timetracker

import de.dude.library.javafx.CleanApp
import de.dude.library.javafx.view.CleanStage
import de.dude.timetracker.controller.MainController
import de.dude.timetracker.repository.Defaults

class App : CleanApp() {

    private lateinit var mainController: MainController

    override fun onStageCreated(stage: CleanStage) {
        stage.apply {
            isHideOnFocusLoss = true
            isAlwaysOnTop = true
            isDraggable = true
            minWidth = Defaults.STAGE_MIN_WIDTH
            minHeight = Defaults.STAGE_MIN_HEIGHT
            enablePersistPosition("TIME_TRACKER_WINDOW")
            enableResizeable(8)
        }
        mainController = MainController(stage)
    }

}