package de.dude.timetracker.controller

import de.dude.library.action.ActionBus
import de.dude.library.javafx.action.LifeCycleAction
import de.dude.library.javafx.view.Navigator
import de.dude.timetracker.action.AppAction
import de.dude.timetracker.action.TrayAction
import de.dude.timetracker.repository.Layouts
import de.dude.timetracker.service.TimerService
import de.dude.timetracker.service.TrayService
import de.dude.timetracker.view.Dialog
import javafx.application.Platform
import javafx.geometry.Pos
import javafx.stage.Stage

class MainController(stage: Stage) : LifeCycleAction, TrayAction, AppAction {

    private val navi = Navigator(stage, Layouts.TIMES)

    init {
        ActionBus.hookForever(this)
        navi.addOverlay(Layouts.ACTIONBAR, Pos.TOP_CENTER)
        TrayService.start()
    }

    override fun onClick() {
        if (TimerService.isRunning) {
            TimerService.stop()
            navi.isShown = true
        } else {
            TimerService.start()
        }
    }

    override fun onRightClick() {
        navi.isShown = true
    }

    override fun exit() {
        if (!TimerService.isRunning || Dialog.confirmExit()) Platform.exit()
    }

    override fun onExit() {
        // TODO Save new times to file
    }

}