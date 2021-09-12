package de.dude.library.javafx

import de.dude.library.extension.tryDo
import de.dude.timetracker.controller.ViewController
import javafx.beans.value.ChangeListener
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import java.util.*

class Layout(val view: Parent, private val controller: ViewController?) {

    companion object {
        private val generalCss = getResource("/styles/general.css")!!.toExternalForm()

        fun load(layoutName: String): Layout {
            val bundle = tryDo { getBundle("strings.$layoutName") }
            val loader = FXMLLoader(getResource("/layouts/$layoutName.fxml"), bundle)
            val view = loader.load<Parent>() ?: throw MissingResourceException(
                "LayoutHelper couldn't load $layoutName",
                Parent::class.simpleName,
                "/layouts/$layoutName.fxml"
            )
            addStyles(view, layoutName)
            return Layout(view, loader.getController<ViewController>())
        }

        private fun addStyles(view: Parent, name: String) = view.stylesheets.apply {
            add(generalCss)
            getResource("/styles/$name.css")?.let { add(it.toExternalForm()) }
        }

        private fun getResource(name: String) = Layout::class.java.getResource(name)
    }

    init {
        setOnCloseListener()
    }

    @Suppress("UNCHECKED_CAST")
    fun <T> getController(): T? = controller as T

    private fun setOnCloseListener() = controller?.apply {
        lateinit var listener: ChangeListener<Parent>
        listener = ChangeListener<Parent> { _, _, container ->
            if (container == null) {
                view.parentProperty().removeListener(listener)
                onClose()
            }
        }
        view.parentProperty().addListener(listener)
    }

}