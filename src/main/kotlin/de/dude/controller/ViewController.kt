package de.dude.controller

import de.dude.action.ActionBus
import de.dude.action.actions.NavigationAction
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

    protected fun goBack() = ActionBus.call<NavigationAction> {
        goBack()
    }

    protected fun goTo(layoutName: String) = ActionBus.call<NavigationAction> {
        goTo(layoutName)
    }

    protected open fun onCreate() {}

}