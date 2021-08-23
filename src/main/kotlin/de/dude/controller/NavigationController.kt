package de.dude.controller

import de.dude.action.ActionBus
import de.dude.action.actions.AppAction
import de.dude.action.actions.LifeCycleAction
import de.dude.util.Bundle
import de.dude.util.Layout
import de.dude.util.canDragWindow
import javafx.fxml.FXML
import javafx.scene.Node
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.layout.Pane
import java.util.*
import kotlin.collections.ArrayDeque

class NavigationController : LifeCycleAction {

    @FXML
    private lateinit var dragContainer: Node

    @FXML
    private lateinit var viewContainer: Pane

    @FXML
    lateinit var destinationTitle: Label

    @FXML
    private lateinit var backButton: Button

    @FXML
    private lateinit var settingsButton: Button

    private val bundle: ResourceBundle by lazy { Bundle.get("strings.navigation") }
    private val history = ArrayDeque<Layout>()

    init {
        ActionBus.hook<LifeCycleAction>(this)
    }

    @FXML
    private fun initialize() {
        dragContainer.canDragWindow()
        goTo("times", true)
    }

    @FXML
    fun goBack(): Layout? {
        if (history.size <= 1) {
            showBackButton(false)
            return null
        }
        history.removeLast()
        val layout = history.last()
        viewContainer.children[0] = layout.view
        return layout
    }

    @FXML
    private fun goToSettings() = goTo("settings")

    @FXML
    private fun exit() = ActionBus.call<AppAction> { exit() }

    fun goTo(layoutName: String, isInitial: Boolean = false) = Layout.load(layoutName).also { layout ->
        showBackButton(!isInitial)
        destinationTitle.text = bundle.getString("destination.$layoutName.title")
        viewContainer.children[0] = layout.view
        history.add(layout)
        layout.getController<ViewController>().putNavigationController(this)
    }

    private fun showBackButton(doShow: Boolean) {
        settingsButton.isVisible = !doShow
        settingsButton.isManaged = !doShow
        backButton.isVisible = doShow
        backButton.isManaged = doShow
    }

    override fun onExit() {
        while (history.isNotEmpty()) {
            history.removeLast().getController<ViewController>().onExit()
        }
    }

}