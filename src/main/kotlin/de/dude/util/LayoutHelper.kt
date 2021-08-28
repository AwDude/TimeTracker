package de.dude.util

import de.dude.util.extension.getBundle
import de.dude.util.extension.tryDo
import javafx.fxml.FXMLLoader
import javafx.scene.Parent

object LayoutHelper {

    private val generalCss = getResource("/styles/general.css")!!.toExternalForm()

    fun load(layoutName: String): Parent {
        val bundle = tryDo { getBundle("strings.$layoutName") }
        val loader = FXMLLoader(getResource("/layouts/$layoutName.fxml"), bundle)
        val view = loader.load<Parent>()
        addStyles(view, layoutName)
        return view
    }

    private fun addStyles(view: Parent, name: String) = view.stylesheets.apply {
        add(generalCss)
        getResource("/styles/$name.css")?.toExternalForm()?.let { add(it) }
    }

    private fun getResource(name: String) = LayoutHelper::class.java.getResource(name)

}