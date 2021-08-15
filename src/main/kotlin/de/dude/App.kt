package de.dude

import de.dude.controller.MainController
import de.dude.controller.`interface`.Exitable
import javafx.application.Application
import javafx.application.Platform
import javafx.stage.Stage
import javafx.stage.StageStyle

class App : Application() {

    companion object {
        @JvmStatic
        fun main(args: Array<String>) = launch(App::class.java, *args)
    }

    private var mainController: Exitable? = null

    override fun start(primaryStage: Stage) {
        val stage = createStage(primaryStage)
        mainController = MainController(stage)
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

    override fun stop() = super.stop().also {
        mainController?.onExit()
        mainController = null
    }

}