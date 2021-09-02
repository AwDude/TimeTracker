package de.dude.library.javafx

import de.dude.library.action.ActionBus
import de.dude.timetracker.action.LifeCycleAction
import de.dude.timetracker.repository.Defaults
import javafx.application.Application
import javafx.application.Platform
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.value.ChangeListener
import javafx.stage.Stage
import javafx.stage.StageStyle

class App : Application() {

    companion object {
        private var stageProperty = SimpleObjectProperty<Stage>()

        fun newStage() = CleanStage().also { newStage ->
            stageProperty.get()?.let { newStage.initOwner(it) } ?: run {
                lateinit var listener: ChangeListener<Stage>
                listener = ChangeListener<Stage> { _, _, stage ->
                    stageProperty.removeListener(listener)
                    newStage.initOwner(stage)
                }
                stageProperty.addListener(listener)
                launch(App::class.java)
            }
        }

        // TODO move to timetracker package
        fun newCleanStage() = newStage().apply {
            isHideOnFocusLoss = true
            isAlwaysOnTop = true
            minWidth = Defaults.STAGE_MIN_WIDTH
            minHeight = Defaults.STAGE_MIN_HEIGHT
            enablePersistPosition("TIME_TRACKER_WINDOW")
            enableResizeable(4)
        }
    }

    override fun start(primaryStage: Stage) = stageProperty.set(primaryStage.apply {
        initStyle(StageStyle.UTILITY)
        opacity = 0.0
        setOnHidden { Platform.exit() }
        show()
    })

    override fun stop() {
        ActionBus.call<LifeCycleAction> { onExit() }
        stageProperty.get()?.close()
        stageProperty.set(null)
    }

}