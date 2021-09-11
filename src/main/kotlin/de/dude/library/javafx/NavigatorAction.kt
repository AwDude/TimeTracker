package de.dude.library.javafx

import de.dude.library.action.ActionBus

interface NavigatorAction : ActionBus.Action {

    val isHomeShown: Boolean

    var isVisible: Boolean

    var isMaximized: Boolean

    fun goTo(layoutName: String)

    fun goBackToHome()

    fun goBack()

    fun setHome(layoutName: String)

    fun setMenuBar(layoutName: String)

}