package de.dude.timetracker.action

import de.dude.library.action.ActionBus

interface AppAction : ActionBus.Action {
    fun exit()
}