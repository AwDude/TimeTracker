package de.dude.timetracker.view

import de.dude.library.javafx.util.getBundle
import de.dude.library.javafx.view.Dialog
import java.util.*

object Dialog {

    private val bundle: ResourceBundle by lazy { getBundle("strings.dialog") }

    fun discardChanges(onConfirm: Runnable? = null) =
        confirmDialog("discard.title", "discard.content", onConfirm)

    fun confirmDelete(onConfirm: Runnable? = null) =
        confirmDialog("confirmDelete.title", "confirmDelete.content", onConfirm)

/*    fun errorSystemTray() {
        alertDialog("errorSystemTray.title", "errorSystemTray.content")
    }*/

    fun confirmExit(onConfirm: Runnable? = null) =
        confirmDialog("confirmExit.title", "confirmExit.content", onConfirm)

    private fun confirmDialog(titleKey: String, contentKey: String, onConfirm: Runnable? = null) =
        Dialog.confirmDialog(bundle.getString(titleKey), bundle.getString(contentKey), onConfirm)

}