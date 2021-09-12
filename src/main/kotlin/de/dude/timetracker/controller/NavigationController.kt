package de.dude.timetracker.controller

import de.dude.library.action.ActionBus
import de.dude.library.javafx.Layout
import de.dude.library.javafx.canDragWindow
import de.dude.timetracker.action.AppAction
import de.dude.timetracker.action.LifeCycleAction
import de.dude.timetracker.action.NavigationAction
import javafx.fxml.FXML
import javafx.scene.Node
import javafx.scene.Parent
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.layout.Pane

class NavigationController : ViewController(), LifeCycleAction, NavigationAction {

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

    private val history = ArrayDeque<Parent>()

    override fun onCreate() {
        ActionBus.hook(this)
        dragContainer.canDragWindow()
        goTo("times")
    }

    @FXML
    private fun exit() = ActionBus.call<AppAction> { exit() }

    @FXML
    fun goBack() {
        history.removeLast()
        viewContainer.children[0] = history.last()
        updateNavigationButtons()
    }

    @FXML
    private fun goToSettings() = goTo("settings")

    override fun goTo(layoutName: String) = Layout.load(layoutName).view.let { view ->
        destinationTitle.text = resources?.getString("destination.$layoutName.title") ?: ""
        viewContainer.children[0] = view
        history.add(view)
        updateNavigationButtons()
    }

    private fun updateNavigationButtons() {
        val showBackButton = history.size > 1
        settingsButton.isVisible = !showBackButton
        settingsButton.isManaged = !showBackButton
        backButton.isVisible = showBackButton
        backButton.isManaged = showBackButton
    }

    override fun onExit() {
        viewContainer.children.clear()
        history.clear()
    }

}