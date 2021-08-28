package de.dude.timetracker.controller

import de.dude.library.action.ActionBus
import de.dude.timetracker.action.LifeCycleAction
import javafx.fxml.FXML
import javafx.scene.control.ChoiceBox
import javafx.scene.control.TextField

class SettingsController : ViewController(), LifeCycleAction {

    /*
        - dark or light theme
        - minimize to tray on close
            - minimized on start up
        - window always on top
        - continue timer while editing
     */
    init {
        ActionBus.hook(this, LifeCycleAction::class)
    }

    @FXML
    lateinit var dbTypeChoiceBox: ChoiceBox<String>

    @FXML
    lateinit var dbHostTextInput: TextField

    @FXML
    lateinit var dbPortTextInput: TextField

    @FXML
    lateinit var dbNameTextInput: TextField

    @FXML
    lateinit var dbUserTextInput: TextField

    @FXML
    lateinit var dbPasswordTextInput: TextField

    private var isSaved = false

    private fun saveSettings() {
        if (isSaved) return
        isSaved = true
    }

    // TODO not getting called sometimes. Fix needed!
    override fun onExit() {
        saveSettings()
    }

}