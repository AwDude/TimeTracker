package de.dude.action.actions

import de.dude.action.ActionBus

interface NavigationAction : ActionBus.Action {
    fun goTo(layoutName: String)
    fun goBack()
}