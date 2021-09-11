package de.dude.library.javafx

import javafx.stage.Stage

class Navigator(private val stage: Stage) : NavigatorAction {

    override val isHomeShown: Boolean
        get() = false

    // TODO maximize or fullscreen?aximized: Boolean
    override var isMaximized: Boolean
        get() = stage.isMaximized
        set(value) {
            stage.isMaximized = value
        }

    override var isVisible: Boolean
        get() = stage.isShowing
        set(value) = runOnUI {
            if (value) stage.show() else stage.hide()
        }

    init {
        runOnUI {
            //stage.scene = Scene()
        }
    }

    override fun goTo(layoutName: String) {}

    override fun goBackToHome() {}

    override fun goBack() {}

    override fun setHome(layoutName: String) {

    }

    override fun setMenuBar(layoutName: String) {

    }

}