package de.dude.action.actions

import de.dude.action.ActionBus

interface LifeCycleAction : ActionBus.Action {
    fun onExit()
}