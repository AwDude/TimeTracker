package de.dude.library.javafx

import javafx.geometry.Pos
import javafx.scene.control.Button
import javafx.scene.layout.StackPane
import javafx.scene.paint.Color
import javafx.scene.shape.Circle
import javafx.scene.shape.Rectangle

const val WIDTH = 36.0
const val TRACK_HEIGHT = 10.0
const val THUMB_HEIGHT = 16.0

class ToggleSwitch : StackPane() {

    private val back = Rectangle(WIDTH, TRACK_HEIGHT, Color.RED)
    private val button = Button()
    private val buttonStyleOff = "-fx-background-color: #e1e6e8;"
    private val buttonStyleOn = "-fx-background-color: #00893d;"
    private var onToggle: ((Boolean) -> Unit)? = null

    private var state = false

    init {
        initViews()
        setOnMouseClicked { toggle() }
        button.setOnMouseClicked { toggle() }
    }

    fun setOnToggle(listener: ((Boolean) -> Unit)?) {
        onToggle = listener
    }

    fun toggle() {
        if (state) {
            button.style = buttonStyleOff
            back.fill = Color.valueOf("#ced5da")
            setAlignment(button, Pos.CENTER_LEFT)
        } else {
            button.style = buttonStyleOn
            back.fill = Color.valueOf("#80C49E")
            setAlignment(button, Pos.CENTER_RIGHT)
        }
        state = !state
        onToggle?.invoke(state)
    }

    fun getState() = state

    fun setState(newState: Boolean) {
        if (state != newState) toggle()
    }

    private fun initViews() {
        children.addAll(back, button)
        setMinSize(WIDTH, THUMB_HEIGHT)
        setMaxSize(WIDTH, THUMB_HEIGHT)
        back.maxWidth(WIDTH)
        back.minWidth(WIDTH)
        back.maxHeight(TRACK_HEIGHT)
        back.minHeight(TRACK_HEIGHT)
        back.arcHeight = back.height
        back.arcWidth = back.height
        back.fill = Color.valueOf("#ced5da")
        val r = 2.0
        button.shape = Circle(r)
        setAlignment(button, Pos.CENTER_LEFT)
        button.setMaxSize(THUMB_HEIGHT, THUMB_HEIGHT)
        button.setMinSize(THUMB_HEIGHT, THUMB_HEIGHT)
        button.style = buttonStyleOff
        button.isFocusTraversable = false
    }

}