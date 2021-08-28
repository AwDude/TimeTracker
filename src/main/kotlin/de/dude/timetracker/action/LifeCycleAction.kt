package de.dude.timetracker.action

import de.dude.library.action.ActionBus

interface LifeCycleAction : ActionBus.Action {
    fun onExit()
}