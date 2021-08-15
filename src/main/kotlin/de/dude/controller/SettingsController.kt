package de.dude.controller

import javafx.fxml.FXML
import javafx.scene.control.ChoiceBox
import javafx.scene.control.TextField

class SettingsController : ViewController() {

    /*
        - dark or light theme
        - minimize to tray on close
            - minimized on start up
        - window always on top
        - continue timer while editing
     */


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

    override fun onExit() = saveSettings()

}