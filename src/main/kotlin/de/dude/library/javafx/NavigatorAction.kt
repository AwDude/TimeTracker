package de.dude.library.javafx

import de.dude.library.action.ActionBus

interface NavigatorAction : ActionBus.Action {

    val isHomeShown: Boolean

    var isShown: Boolean

    var isMaximized: Boolean

    fun goTo(layoutName: String)

    fun goBackToHome()

    fun goBack()

    fun setHome(layoutName: String)

}