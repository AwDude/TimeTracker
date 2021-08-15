package de.dude.view

import de.dude.util.Bundle
import javafx.scene.control.Alert
import javafx.scene.control.ButtonType
import java.util.*

object Dialog {

    private val bundle: ResourceBundle by lazy { Bundle.get("strings.dialog") }

    fun discardChanges(onConfirm: Runnable? = null) =
        confirmDialog("discard.title", "discard.content", onConfirm)

    fun confirmDelete(onConfirm: Runnable? = null) =
        confirmDialog("confirmDelete.title", "confirmDelete.content", onConfirm)

    fun errorSystemTray() {
        alertDialog("errorSystemTray.title", "errorSystemTray.content")
    }

    fun confirmExit(onConfirm: Runnable? = null) =
        confirmDialog("confirmExit.title", "confirmExit.content", onConfirm)

    private fun confirmDialog(titleKey: String, contentKey: String, onConfirm: Runnable? = null): Boolean {
        val result = Alert(Alert.AlertType.CONFIRMATION).apply {
            title = bundle.getString(titleKey)
            headerText = null
            contentText = bundle.getString(contentKey)
        }.showAndWait()
        val confirmed = result.isPresent && result.get() == ButtonType.OK
        if (confirmed) onConfirm?.run()
        return confirmed
    }

    @Suppress("SameParameterValue")
    private fun alertDialog(titleKey: String, contentKey: String) = Alert(Alert.AlertType.CONFIRMATION).apply {
        title = bundle.getString(titleKey)
        headerText = null
        contentText = bundle.getString(contentKey)
    }.showAndWait()

}