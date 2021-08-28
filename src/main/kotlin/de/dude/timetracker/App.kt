package de.dude.timetracker

import de.dude.library.action.ActionBus
import de.dude.library.javafx.CleanStage
import de.dude.timetracker.action.LifeCycleAction
import de.dude.timetracker.controller.MainController
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
        //ActionBus.log = ::println
        primaryStage.apply {
            initStyle(StageStyle.UTILITY)
            opacity = 0.0
            setOnHidden { Platform.exit() }
            show()
        }
        MainController(CleanStage(primaryStage))
    }

    override fun stop() = ActionBus.call<LifeCycleAction> { onExit() }

}