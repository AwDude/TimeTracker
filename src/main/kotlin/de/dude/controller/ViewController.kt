package de.dude.controller

import javafx.fxml.Initializable
import java.net.URL
import java.util.*

open class ViewController : Initializable {

    protected var resources: ResourceBundle? = null
    private var navigationController: NavigationController? = null

    override fun initialize(location: URL?, resources: ResourceBundle?) {
        this.resources = resources
    }

    fun putNavigationController(controller: NavigationController) {
        this.navigationController = controller
        onCreate()
    }

    protected fun goBack() = navigationController?.goBack().also { navigationController = null }

    protected fun goTo(layoutName: String) = navigationController?.goTo(layoutName)

    protected open fun onCreate() {}

    open fun onExit() {}

}