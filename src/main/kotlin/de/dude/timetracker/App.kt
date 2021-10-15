package de.dude.timetracker

import de.dude.library.javafx.CleanApp
import de.dude.library.javafx.view.CleanStage
import de.dude.library.repository.database.Entity
import de.dude.timetracker.controller.MainController
import de.dude.timetracker.repository.Defaults
import javafx.application.Platform

class App : CleanApp() {

    override fun onStageCreated(stage: CleanStage) {
        Entity()
        //MMF()
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

}