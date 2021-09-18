package de.dude.library.javafx.view

import de.dude.library.extension.noneNull
import de.dude.library.javafx.util.StageDragger
import de.dude.library.javafx.util.StageResizer
import de.dude.library.javafx.util.getTaskBarHeight
import de.dude.library.javafx.util.isInScreen
import de.dude.library.repository.LibStore
import javafx.beans.value.ChangeListener
import javafx.geometry.Point2D
import javafx.stage.Screen
import javafx.stage.Stage
import javafx.stage.StageStyle

@Suppress("MemberVisibilityCanBePrivate")
class CleanStage : Stage() {

    var isDraggable: Boolean
        get() = dragger != null
        set(value) = makeDraggable(value)

    val isResizeable: Boolean
        get() = resizer != null

    val isPersistPosition: Boolean
        get() = windowKey != null

    var isHideOnFocusLoss: Boolean
        get() = focusLossListener != null
        set(value) = hideOnFocusLoss(value)

    private var focusLossListener: ChangeListener<Boolean>? = null
    private var dragger: StageDragger? = null
    private var resizer: StageResizer? = null
    private var windowKey: String? = null

    init {
        initStyle(StageStyle.UNDECORATED)
    }

    fun enableResizeable(resizeArea: Int = 4) {
        disableResizeable()
        resizer = StageResizer(this, resizeArea) { x, y, width, height ->
            windowKey?.let {
                LibStore.setStageX(it, x)
                LibStore.setStageY(it, y)
                LibStore.setStageWidth(it, width)
                LibStore.setStageHeight(it, height)
            }
        }
    }

    fun disableResizeable() {
        resizer?.stop()
        resizer = null
    }

    fun enablePersistPosition(windowKey: String) {
        this.windowKey = windowKey
        setInitialStagePosition()
    }

    fun disablePersistPosition() {
        windowKey = null
    }

    private fun setInitialStagePosition() = windowKey?.let { key ->
        val tarX = LibStore.getStageX(key)
        val tarY = LibStore.getStageY(key)
        val tarWidth = LibStore.getStageWidth(key)
        val tarHeight = LibStore.getStageHeight(key)
        if (noneNull(tarX, tarY, tarWidth, tarHeight)) {
            val bestPos = getBestStagePosition(tarX!!, tarY!!, tarWidth!!, tarHeight!!)
            x = bestPos.x
            y = bestPos.y
            width = tarWidth
            height = tarHeight
        }
    }

    private fun getBestStagePosition(tarX: Double, tarY: Double, tarWidth: Double, tarHeight: Double): Point2D {
        if (isInScreen(tarX, tarY, tarWidth, tarHeight)) {
            return Point2D(tarX, tarY)
        }
        val screenBounds = Screen.getPrimary().bounds
        val newX = screenBounds.maxX - tarWidth
        val newY = screenBounds.maxY - tarHeight - getTaskBarHeight() - 1
        return Point2D(newX, newY)
    }

    private fun hideOnFocusLoss(doHide: Boolean) {
        if (doHide) {
            if (focusLossListener == null) {
                focusLossListener = ChangeListener { _, _, isFocused -> if (!isFocused) hide() }
                focusedProperty().addListener(focusLossListener)
            }
        } else {
            focusLossListener?.let { focusedProperty().removeListener(it) }
        }
    }

    private fun makeDraggable(isDraggable: Boolean) {
        dragger = if (isDraggable) {
            if (dragger != null) return
            StageDragger(this) { x, y ->
                windowKey?.let {
                    LibStore.setStageX(it, x)
                    LibStore.setStageY(it, y)
                }
            }
        } else {
            dragger?.stop()
            null
        }
    }

}