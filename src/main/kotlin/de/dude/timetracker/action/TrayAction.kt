package de.dude.timetracker.action

import de.dude.library.action.ActionBus

interface TrayAction : ActionBus.Action {
    fun onClick()
    fun onRightClick()
}