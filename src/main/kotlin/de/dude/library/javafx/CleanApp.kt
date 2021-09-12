package de.dude.library.javafx

import de.dude.library.action.ActionBus
import de.dude.timetracker.action.LifeCycleAction
import javafx.application.Application
import javafx.application.Platform
import javafx.stage.Stage
import javafx.stage.StageStyle

abstract class CleanApp : Application() {

    protected abstract fun onStageCreated(stage: CleanStage)

    override fun start(primaryStage: Stage) {
        primaryStage.apply {
            initStyle(StageStyle.UTILITY)
            opacity = 0.0
            setOnHidden { Platform.exit() }
            show()
        }
        val cleanStage = CleanStage().apply { initOwner(primaryStage) }
        onStageCreated(cleanStage)
    }

    override fun stop() = ActionBus.call<LifeCycleAction> { onExit() }

}