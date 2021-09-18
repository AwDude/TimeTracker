package de.dude.timetracker.controller

import de.dude.library.action.ActionBus
import de.dude.library.extension.void
import de.dude.library.javafx.view.NavigatorOverlay
import de.dude.timetracker.action.AppAction
import javafx.fxml.FXML
import javafx.scene.control.Button
import javafx.scene.control.Label

class ActionbarController : NavigatorOverlay() {

    @FXML
    private lateinit var destinationTitle: Label

    @FXML
    private lateinit var backButton: Button

    @FXML
    private lateinit var settingsButton: Button

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
    private fun exit() = ActionBus.call<AppAction> { exit() }

}