package de.dude.library.javafx.action

import de.dude.library.action.ActionBus

interface LifeCycleAction : ActionBus.Action {
    fun onExit()
}