package de.dude.timetracker.controller

import javafx.fxml.Initializable
import java.net.URL
import java.util.*

open class ViewController : Initializable {

    @Suppress("MemberVisibilityCanBePrivate")
    protected var resources: ResourceBundle? = null

    override fun initialize(location: URL?, resources: ResourceBundle?) {
        this.resources = resources
        onCreate()
    }

    protected open fun onCreate() {}

    open fun onClose() {}

}