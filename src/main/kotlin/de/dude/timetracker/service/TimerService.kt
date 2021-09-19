package de.dude.timetracker.service

import de.dude.library.action.ActionBus
import de.dude.library.extension.void
import de.dude.library.javafx.action.LifeCycleAction
import de.dude.timetracker.action.TimerAction
import java.util.*
import kotlin.concurrent.scheduleAtFixedRate


private const val MIN_IN_MS = 100L//60000L

object TimerService : LifeCycleAction, Service {
    @Volatile
    private var totalMinutes = 0
    private var timer: Timer? = null

    val isRunning: Boolean
        get() = timer != null

    init {
        ActionBus.hook(this)
    }

    override fun start() {
        totalMinutes = -1
        timer = Timer(true).apply {
            scheduleAtFixedRate(0, MIN_IN_MS) { updateTime() }
        }
    }

    override fun stop() = timer?.apply {
        cancel()
        purge()
        timer = null
        ActionBus.call<TimerAction> { onStop() }
    }.void

    private fun updateTime() {
        totalMinutes++
        val hours = if (totalMinutes > 0) totalMinutes / 60 else 0
        val minutes = totalMinutes % 60
        ActionBus.call<TimerAction> { onUpdate(hours, minutes) }
    }

    override fun onExit() = stop()

}