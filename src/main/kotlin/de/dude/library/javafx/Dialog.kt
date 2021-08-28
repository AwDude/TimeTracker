package de.dude.library.javafx

import javafx.scene.control.Alert
import javafx.scene.control.ButtonType

object Dialog {

    fun confirmDialog(title: String, content: String, onConfirm: Runnable? = null): Boolean {
        val result = Alert(Alert.AlertType.CONFIRMATION).apply {
            this.title = title
            headerText = null
            contentText = content
        }.showAndWait()
        val confirmed = result.isPresent && result.get() == ButtonType.OK
        if (confirmed) onConfirm?.run()
        return confirmed
    }

}