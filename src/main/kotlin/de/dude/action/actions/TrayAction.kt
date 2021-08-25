package de.dude.action.actions

import de.dude.action.ActionBus

interface TrayAction : ActionBus.Action {
    fun onClick()
    fun onRightClick()
}