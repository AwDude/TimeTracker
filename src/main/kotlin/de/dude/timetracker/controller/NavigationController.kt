package de.dude.timetracker.controller

import de.dude.library.action.ActionBus
import de.dude.library.javafx.LayoutHelper
import de.dude.library.javafx.canDragWindow
import de.dude.library.javafx.getBundle
import de.dude.timetracker.action.AppAction
import de.dude.timetracker.action.LifeCycleAction
import de.dude.timetracker.action.NavigationAction
import javafx.fxml.FXML
import javafx.scene.Node
import javafx.scene.Parent
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.layout.Pane
import java.util.*
import kotlin.collections.ArrayDeque

class NavigationController : LifeCycleAction, NavigationAction {

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

    private val bundle: ResourceBundle by lazy { getBundle("strings.navigation") }
    private val history = ArrayDeque<Parent>()

    init {
        ActionBus.hook(this)
    }

    @FXML
    private fun initialize() {
        dragContainer.canDragWindow()
        goTo("times")
    }

    @FXML
    private fun exit() = ActionBus.call<AppAction> { exit() }

    @FXML
    private fun goBack() {
        history.removeLast()
        viewContainer.children[0] = history.last()
        updateNavigationButtons()
    }

    @FXML
    private fun goToSettings() = goTo("settings")

    override fun goTo(layoutName: String) {
        LayoutHelper.load(layoutName).also { view ->
            destinationTitle.text = bundle.getString("destination.$layoutName.title")
            viewContainer.children[0] = view
            history.add(view)
            updateNavigationButtons()
        }
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