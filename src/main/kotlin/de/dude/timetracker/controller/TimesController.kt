package de.dude.timetracker.controller

import de.dude.timetracker.service.TimerService
import javafx.fxml.FXML

class TimesController : ViewController() {

    @FXML
    private fun stop() {
        TimerService.stop()
    }
}