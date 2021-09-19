package de.dude.timetracker.controller

import de.dude.library.action.ActionBus
import de.dude.library.extension.void
import de.dude.library.javafx.view.NavigatorOverlay
import de.dude.timetracker.action.AppAction
import javafx.fxml.FXML
import javafx.scene.control.Button

class ActionbarController : NavigatorOverlay() {

    @FXML
    private lateinit var backButton: Button

    @FXML
    private lateinit var settingsButton: Button

    @FXML
    private lateinit var enterFullScreenButton: Button

    @FXML
    private lateinit var exitFullScreenButton: Button

    override fun onLayoutChange() = navigator?.apply {
        settingsButton.isVisible = isHomeShown
        settingsButton.isManaged = isHomeShown
        backButton.isVisible = !isHomeShown
        backButton.isManaged = !isHomeShown
    }.void

    @FXML
    private fun goToSettings() = navigator?.goTo("settings")

    @FXML
    private fun goBack() = navigator?.goBack()

    @FXML
    fun minimize() {
        navigator?.isShown = false
    }

    @FXML
    fun enterFullScreen() = showFullScreen(true)

    @FXML
    fun exitFullScreen() = showFullScreen(false)

    @FXML
    private fun exit() = ActionBus.call<AppAction> { exit() }

    private fun showFullScreen(inFullScreen: Boolean) = navigator?.apply {
        isMaximized = inFullScreen
        enterFullScreenButton.isVisible = !inFullScreen
        enterFullScreenButton.isManaged = !inFullScreen
        exitFullScreenButton.isVisible = inFullScreen
        exitFullScreenButton.isManaged = inFullScreen
    }

}