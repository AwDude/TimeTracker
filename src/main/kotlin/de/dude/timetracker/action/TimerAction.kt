package de.dude.timetracker.action

import de.dude.library.action.ActionBus

interface TimerAction : ActionBus.Action {
    fun onStart() {}
    fun onStop() {}
    fun onUpdate(hours: Int, minutes: Int) {}
}