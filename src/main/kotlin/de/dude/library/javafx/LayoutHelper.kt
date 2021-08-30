package de.dude.library.javafx

import de.dude.library.extension.tryDo
import de.dude.timetracker.controller.ViewController
import javafx.beans.value.ChangeListener
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import java.util.*

object LayoutHelper {

    private val generalCss = getResource("/styles/general.css")!!.toExternalForm()

    fun load(layoutName: String): Parent {
        val bundle = tryDo { getBundle("strings.$layoutName") }
        val loader = FXMLLoader(getResource("/layouts/$layoutName.fxml"), bundle)
        val view = loader.load<Parent>() ?: throw MissingResourceException(
            "LayoutHelper couldn't load $layoutName",
            Parent::class.simpleName,
            "/layouts/$layoutName.fxml"
        )
        addStyles(view, layoutName)
        setOnCloseListener(view, loader)
        return view
    }

    private fun addStyles(view: Parent, name: String) = view.stylesheets.apply {
        add(generalCss)
        getResource("/styles/$name.css")?.toExternalForm()?.let { add(it) }
    }

    private fun getResource(name: String) = LayoutHelper::class.java.getResource(name)

    private fun setOnCloseListener(view: Parent, loader: FXMLLoader) = loader.getController<ViewController>()?.apply {
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