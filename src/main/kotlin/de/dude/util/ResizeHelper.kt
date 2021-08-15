package de.dude.util

import de.dude.repository.Settings
import javafx.event.EventHandler
import javafx.scene.Cursor
import javafx.scene.input.MouseEvent
import javafx.stage.Stage

class ResizeHelper(private val stage: Stage, private val resizeArea: Int) {
    private val listeners = HashMap<Cursor, EventHandler<MouseEvent>>()
    private var prevSceneX = 0.0
    private var prevSceneY = 0.0
    private var prevScreenX = 0.0
    private var prevScreenY = 0.0
    private var prevStageWidth = 0.0
    private var prevStageHeight = 0.0
    private var hasSizeChanged = false

    init {
        createListeners()
        launch()
    }

    private fun createListeners() {
        listeners[Cursor.NW_RESIZE] = EventHandler { event: MouseEvent ->
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
        }
        listeners[Cursor.NE_RESIZE] = EventHandler { event: MouseEvent ->
            val newWidth = prevStageWidth - (event.screenX - prevScreenX)
            val newHeight = prevStageHeight + (event.screenY - prevScreenY)
            if (newHeight > stage.minHeight) stage.height = newHeight
            if (newWidth > stage.minWidth) {
                stage.x = event.screenX - prevSceneX
                stage.width = newWidth
            }
        }
        listeners[Cursor.SW_RESIZE] = EventHandler { event: MouseEvent ->
            val newWidth = prevStageWidth + (event.screenX - prevScreenX)
            val newHeight = prevStageHeight - (event.screenY - prevScreenY)
            if (newHeight > stage.minHeight) {
                stage.height = newHeight
                stage.y = event.screenY - prevSceneY
            }
            if (newWidth > stage.minWidth) stage.width = newWidth
        }
        listeners[Cursor.SE_RESIZE] = EventHandler { event: MouseEvent ->
            val newWidth = prevStageWidth + (event.screenX - prevScreenX)
            val newHeight = prevStageHeight + (event.screenY - prevScreenY)
            if (newHeight > stage.minHeight) stage.height = newHeight
            if (newWidth > stage.minWidth) stage.width = newWidth
        }
        listeners[Cursor.E_RESIZE] = EventHandler { event: MouseEvent ->
            val newWidth = prevStageWidth - (event.screenX - prevScreenX)
            if (newWidth > stage.minWidth) {
                stage.x = event.screenX - prevSceneX
                stage.width = newWidth
            }
        }
        listeners[Cursor.W_RESIZE] = EventHandler { event: MouseEvent ->
            val newWidth = prevStageWidth + (event.screenX - prevScreenX)
            if (newWidth > stage.minWidth) stage.width = newWidth
        }
        listeners[Cursor.N_RESIZE] = EventHandler { event: MouseEvent ->
            val newHeight = prevStageHeight - (event.screenY - prevScreenY)
            if (newHeight > stage.minHeight) {
                stage.y = event.screenY - prevSceneY
                stage.height = newHeight
            }
        }
        listeners[Cursor.S_RESIZE] = EventHandler { event: MouseEvent ->
            val newHeight = prevStageHeight + (event.screenY - prevScreenY)
            if (newHeight > stage.minHeight && isInScreen(event.screenX, event.screenY)) stage.height = newHeight
        }
    }

    private fun launch() {
        stage.scene.setOnMouseReleased {
            if (hasSizeChanged) {
                Settings.stageX = stage.x
                Settings.stageY = stage.y
                Settings.stageHeight = stage.height
                Settings.stageWidth = stage.width
                hasSizeChanged = false
            }
        }
        stage.scene.onMousePressed = EventHandler { event: MouseEvent ->
            if (stage.scene.cursor === Cursor.DEFAULT) return@EventHandler
            prevSceneX = event.sceneX
            prevSceneY = event.sceneY
            prevScreenX = event.screenX
            prevScreenY = event.screenY
            prevStageWidth = stage.width
            prevStageHeight = stage.height
            hasSizeChanged = true
        }
        stage.scene.onMouseMoved = EventHandler { event: MouseEvent ->
            val sx = event.sceneX
            val sy = event.sceneY
            val leftTrigger = sx > 0 && sx < resizeArea
            val rightTrigger = sx < stage.scene.width && sx > stage.scene.width - resizeArea
            val upperTrigger = sy < stage.scene.height && sy > stage.scene.height - resizeArea
            val lowerTrigger = sy > 0 && sy < resizeArea

            if (leftTrigger && lowerTrigger) fireAction(Cursor.NW_RESIZE)
            else if (leftTrigger && upperTrigger) fireAction(Cursor.NE_RESIZE)
            else if (rightTrigger && lowerTrigger) fireAction(Cursor.SW_RESIZE)
            else if (rightTrigger && upperTrigger) fireAction(Cursor.SE_RESIZE)
            else if (leftTrigger) fireAction(Cursor.E_RESIZE)
            else if (rightTrigger) fireAction(Cursor.W_RESIZE)
            else if (lowerTrigger) fireAction(Cursor.N_RESIZE)
            else if (upperTrigger && sy >= resizeArea) fireAction(Cursor.S_RESIZE)
            else fireAction(Cursor.DEFAULT)
        }
    }

    private fun fireAction(cursor: Cursor) {
        stage.scene.cursor = cursor
        if (cursor !== Cursor.DEFAULT) stage.scene.onMouseDragged = listeners[cursor]
        else stage.scene.onMouseDragged = null
    }
}