package de.dude.util

import javafx.fxml.FXMLLoader
import javafx.scene.Parent

class Layout private constructor(val view: Parent, private val loader: FXMLLoader) {

    fun <T> getController(): T = loader.getController()

    companion object {

        private val generalCss = getResource("/styles/general.css")!!.toExternalForm()

        fun load(layoutName: String): Layout {
            val bundle = Bundle.get("strings.$layoutName")
            val loader = FXMLLoader(getResource("/layouts/$layoutName.fxml"), bundle)
            val view = loader.load<Parent>()
            view.stylesheets.add(generalCss)
            addStyleSheet(view, layoutName)
            return Layout(view, loader)
        }

        private fun addStyleSheet(view: Parent, name: String) {
            getResource("/styles/$name.css")?.toExternalForm()?.let { styleSheet ->
                view.stylesheets.add(styleSheet)
            }
        }

        private fun getResource(name: String) = Layout::class.java.getResource(name)

    }

}