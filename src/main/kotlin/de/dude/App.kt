package de.dude

import de.dude.action.ActionBus
import de.dude.action.actions.LifeCycleAction
import de.dude.controller.MainController
import javafx.application.Application
import javafx.application.Platform
import javafx.stage.Stage
import javafx.stage.StageStyle

class App : Application() {

    companion object {
        @JvmStatic
        fun main(args: Array<String>) = launch(App::class.java, *args)
    }

    override fun start(primaryStage: Stage) {
        val stage = createStage(primaryStage)
        MainController(stage)
    }

    private fun createStage(primaryStage: Stage): Stage {
        primaryStage.apply {
            initStyle(StageStyle.UTILITY)
            opacity = 0.0
            setOnHidden { Platform.exit() }
            show()
        }
        return Stage().apply {
            initOwner(primaryStage)
            initStyle(StageStyle.UNDECORATED)
            isAlwaysOnTop = true
        }
    }

    override fun stop() = ActionBus.call<LifeCycleAction> { onExit() }

}