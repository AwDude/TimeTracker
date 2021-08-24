package de.dude.action.actions

import de.dude.action.Action

interface TrayAction : Action {
    fun onClick()
    fun onRightClick()
}