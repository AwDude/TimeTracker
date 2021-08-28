package de.dude.timetracker.action

import de.dude.library.action.ActionBus

interface NavigationAction : ActionBus.Action {
    fun goTo(layoutName: String)
}