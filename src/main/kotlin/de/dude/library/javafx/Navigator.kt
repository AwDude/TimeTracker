package de.dude.library.javafx

import de.dude.library.action.ActionBus
import de.dude.timetracker.action.LifeCycleAction
import javafx.scene.Node
import javafx.scene.Scene
import javafx.scene.layout.StackPane
import javafx.stage.Stage
import java.util.*

class Navigator(private val stage: Stage, homeLayout: String) : NavigatorAction, LifeCycleAction {

    private val container = StackPane()
    private val history = LinkedList<Node>()

    override val isHomeShown: Boolean
        get() = false

    // TODO maximize or fullscreen
    override var isMaximized: Boolean
        get() = stage.isMaximized
        set(value) {
            stage.isMaximized = value
        }

    override var isShown: Boolean
        get() = stage.isShowing
        set(value) = runOnUI {
            if (value) stage.show() else stage.hide()
        }

    init {
        ActionBus.hook(this)
        setHome(homeLayout)
        runOnUI {
            stage.scene = Scene(container)
        }
    }

    override fun goTo(layoutName: String) = runOnUI {
        val view = Layout.load(layoutName).view
        val lastView = container.children.set(0, view)
        history.add(lastView)
    }

    override fun goBackToHome() {
        if (history.isNotEmpty()) {
            container.children[0] = history[0]
            history.clear()
        }
    }

    override fun goBack() = runOnUI {
        history.removeLastOrNull()?.let {
            container.children[0] = it
        }
    }

    override fun setHome(layoutName: String) = runOnUI {
        val view = Layout.load(layoutName).view
        if (container.children.isEmpty()) {
            container.children.add(view)
        } else {
            container.children[0] = view
            if (history.isNotEmpty()) {
                history[0] = view
            }
        }
    }

    override fun onExit() {
        container.children.clear()
        history.clear()
    }

}