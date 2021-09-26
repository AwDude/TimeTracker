package de.dude.timetracker.controller

import javafx.fxml.FXML
import javafx.scene.control.ChoiceBox
import javafx.scene.control.TextField

class SettingsController : ViewController() {

    /*
        - uploading times via jira and Tempo api
        - floor times when uploading
        - show breaks in times overview
        - language
        - start timer on app start
        - dark or light theme
        - minimize to tray
            - minimized on start up
            - minimize on focus loss
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

    // TODO not getting called sometimes. Fix needed!
    override fun onClose() {
        println("Settings onClose")
        saveSettings()
    }

}