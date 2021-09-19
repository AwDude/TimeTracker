package de.dude.library.javafx.util

import javafx.beans.value.ChangeListener
import javafx.event.EventHandler
import javafx.scene.Cursor
import javafx.scene.Scene
import javafx.scene.input.MouseEvent
import javafx.stage.Stage

class StageResizer(
    private val stage: Stage, resizeArea: Int,
    onPersistPosition: ((x: Double, y: Double, width: Double, height: Double) -> Unit)? = null
) {
    private var prevSceneX = 0.0
    private var prevSceneY = 0.0
    private var prevScreenX = 0.0
    private var prevScreenY = 0.0
    private var prevStageWidth = 0.0
    private var prevStageHeight = 0.0
    private var currentDragListener: EventHandler<MouseEvent>? = null

    private val pressListener = EventHandler { event: MouseEvent ->
        prevSceneX = event.sceneX
        prevSceneY = event.sceneY
        prevScreenX = event.screenX
        prevScreenY = event.screenY
        prevStageWidth = stage.width
        prevStageHeight = stage.height
        event.consume()
    }
    private val releaseListener = onPersistPosition?.let { onPersist ->
        EventHandler { event: MouseEvent ->
            onPersist(stage.x, stage.y, stage.width, stage.height)
            event.consume()
        }
    }
    private val moveListener = EventHandler { event: MouseEvent ->
        val sx = event.sceneX
        val sy = event.sceneY
        val leftTrigger = sx > 0 && sx < resizeArea
        val rightTrigger = sx < stage.scene.width && sx > stage.scene.width - resizeArea
        val upperTrigger = sy < stage.scene.height && sy > stage.scene.height - resizeArea
        val lowerTrigger = sy > 0 && sy < resizeArea

        when {
            leftTrigger -> when {
                lowerTrigger -> setCursor(Cursor.NW_RESIZE, nwDragListener)
                upperTrigger -> setCursor(Cursor.NE_RESIZE, neDragListener)
                else -> setCursor(Cursor.E_RESIZE, eDragListener)
            }
            rightTrigger -> when {
                lowerTrigger -> setCursor(Cursor.SW_RESIZE, swDragListener)
                upperTrigger -> setCursor(Cursor.SE_RESIZE, seDragListener)
                else -> setCursor(Cursor.W_RESIZE, wDragListener)
            }
            else -> when {
                lowerTrigger -> setCursor(Cursor.N_RESIZE, nDragListener)
                upperTrigger -> setCursor(Cursor.S_RESIZE, sDragListener)
                else -> setCursor(Cursor.DEFAULT, null)
            }
        }
    }
    private val nwDragListener = EventHandler { event: MouseEvent ->
        val newWidth = prevStageWidth - (event.screenX - prevScreenX)
        val newHeight = prevStageHeight - (event.screenY - prevScreenY)
        if (newHeight > stage.minHeight) {
            stage.y = event.screenY - prevSceneY
            stage.height = newHeight
        }
        if (newWidth > stage.minWidth) {
            stage.x = event.screenX - prevSceneX
            stage.width = newWidth
        }
        event.consume()
    }
    private val neDragListener = EventHandler { event: MouseEvent ->
        val newWidth = prevStageWidth - (event.screenX - prevScreenX)
        val newHeight = prevStageHeight + (event.screenY - prevScreenY)
        if (newHeight > stage.minHeight) stage.height = newHeight
        if (newWidth > stage.minWidth) {
            stage.x = event.screenX - prevSceneX
            stage.width = newWidth
        }
        event.consume()
    }
    private val swDragListener = EventHandler { event: MouseEvent ->
        val newWidth = prevStageWidth + (event.screenX - prevScreenX)
        val newHeight = prevStageHeight - (event.screenY - prevScreenY)
        if (newHeight > stage.minHeight) {
            stage.height = newHeight
            stage.y = event.screenY - prevSceneY
        }
        if (newWidth > stage.minWidth) stage.width = newWidth
        event.consume()
    }
    private val seDragListener = EventHandler { event: MouseEvent ->
        val newWidth = prevStageWidth + (event.screenX - prevScreenX)
        val newHeight = prevStageHeight + (event.screenY - prevScreenY)
        if (newHeight > stage.minHeight) stage.height = newHeight
        if (newWidth > stage.minWidth) stage.width = newWidth
        event.consume()
    }
    private val eDragListener = EventHandler { event: MouseEvent ->
        val newWidth = prevStageWidth - (event.screenX - prevScreenX)
        if (newWidth > stage.minWidth) {
            stage.x = event.screenX - prevSceneX
            stage.width = newWidth
        }
        event.consume()
    }
    private val wDragListener = EventHandler { event: MouseEvent ->
        val newWidth = prevStageWidth + (event.screenX - prevScreenX)
        if (newWidth > stage.minWidth) stage.width = newWidth
        event.consume()
    }
    private val nDragListener = EventHandler { event: MouseEvent ->
        val newHeight = prevStageHeight - (event.screenY - prevScreenY)
        if (newHeight > stage.minHeight) {
            stage.y = event.screenY - prevSceneY
            stage.height = newHeight
        }
        event.consume()
    }
    private val sDragListener = EventHandler { event: MouseEvent ->
        val newHeight = prevStageHeight + (event.screenY - prevScreenY)
        if (newHeight > stage.minHeight && isInScreen(event.screenX, event.screenY)) stage.height = newHeight
        event.consume()
    }
    private val sceneListener = ChangeListener<Scene> { _, oldScene, newScene ->
        removeListeners(oldScene)
        newScene.addEventFilter(MouseEvent.MOUSE_MOVED, moveListener)
    }

    init {
        start()
    }

    fun start() {
        stage.scene?.addEventFilter(MouseEvent.MOUSE_MOVED, moveListener)
        stage.sceneProperty().addListener(sceneListener)
    }

    fun stop() {
        stage.sceneProperty().removeListener(sceneListener)
        removeListeners(stage.scene)
    }

    private fun removeListeners(scene: Scene?) = scene?.apply {
        releaseListener?.let {
            removeEventFilter(MouseEvent.MOUSE_RELEASED, it)
        }
        removeEventFilter(MouseEvent.MOUSE_PRESSED, pressListener)
        removeEventFilter(MouseEvent.MOUSE_MOVED, moveListener)
        currentDragListener?.let {
            removeEventFilter(MouseEvent.MOUSE_DRAGGED, it)
        }
    }

    private fun setCursor(cursor: Cursor, dragListener: EventHandler<MouseEvent>?) = stage.scene.apply {
        if (this.cursor === cursor) return@apply
        this.cursor = cursor
        currentDragListener?.let {
            removeEventFilter(MouseEvent.MOUSE_DRAGGED, it)
        }
        currentDragListener = if (cursor === Cursor.DEFAULT) {
            releaseListener?.let {
                removeEventFilter(MouseEvent.MOUSE_RELEASED, it)
            }
            removeEventFilter(MouseEvent.MOUSE_PRESSED, pressListener)
            null
        } else {
            releaseListener?.let {
                addEventFilter(MouseEvent.MOUSE_RELEASED, it)
            }
            addEventFilter(MouseEvent.MOUSE_PRESSED, pressListener)
            addEventFilter(MouseEvent.MOUSE_DRAGGED, dragListener)
            dragListener
        }
    }

}

