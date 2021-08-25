package de.dude.action.actions

import de.dude.action.ActionBus

interface AppAction : ActionBus.Action {
    fun exit()
}