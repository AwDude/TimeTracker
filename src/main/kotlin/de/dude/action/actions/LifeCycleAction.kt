package de.dude.action.actions

import de.dude.action.Action

interface LifeCycleAction : Action {
    fun onExit()
}