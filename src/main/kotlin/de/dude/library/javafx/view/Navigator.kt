package de.dude.library.javafx.view

import de.dude.library.action.ActionBus
import de.dude.library.javafx.action.LifeCycleAction
import de.dude.library.javafx.action.NavigatorAction
import de.dude.library.javafx.util.Layout
import de.dude.library.javafx.util.runOnUI
import de.dude.library.util.void
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.Scene
import javafx.scene.layout.StackPane
import javafx.stage.Stage
import java.util.*

class Navigator(private val stage: Stage, homeLayout: String) : NavigatorAction, LifeCycleAction {

    private val container = StackPane()
    private val history = LinkedList<Node>()
    private val overlays = LinkedList<NavigatorOverlay>()

    override val isHomeShown: Boolean
        get() = history.isEmpty()

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

    fun addOverlay(overlayLayout: String, alignment: Pos) = runOnUI {
        val layout = Layout.load(overlayLayout)
        layout.getController<NavigatorOverlay>()?.let { overlay ->
            overlay.navigator = this
            overlays.add(overlay)
        }
        StackPane.setAlignment(layout.view, alignment)
        container.children.add(layout.view)
    }

    override fun goTo(layoutName: String) = runOnUI {
        val view = Layout.load(layoutName).view
        val lastView = container.children.set(0, view)
        history.add(lastView)
        overlays.forEach(NavigatorOverlay::onLayoutChange)
    }

    override fun goBackToHome() {
        if (history.isNotEmpty()) {
            runOnUI {
                container.children[0] = history[0]
                history.clear()
                overlays.forEach(NavigatorOverlay::onLayoutChange)
            }
        }
    }

    override fun goBack() = history.removeLastOrNull()?.let {
        runOnUI {
            container.children[0] = it
            overlays.forEach(NavigatorOverlay::onLayoutChange)
        }
    }.void

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