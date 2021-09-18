package de.dude.library.javafx.util

import javafx.beans.value.ChangeListener
import javafx.event.EventHandler
import javafx.scene.Scene
import javafx.scene.input.MouseEvent
import javafx.stage.Stage

class StageDragger(private val stage: Stage, onPersistPosition: ((x: Double, y: Double) -> Unit)? = null) {

    private var xOffset = 0.0
    private var yOffset = 0.0
    private var isDragging = false

    private val sceneListener = ChangeListener<Scene> { _, oldScene, newScene ->
        removeListeners(oldScene)
        addListeners(newScene)
    }
    private val dragListener = EventHandler<MouseEvent> { event ->
        if (isDragging) {
            stage.scene?.apply {
                val newX = event.screenX - xOffset
                val newY = event.screenY - yOffset
                if (isInScreen(newX, event.screenY, width, 1.0)) window.x = newX
                if (isInScreen(event.screenX, newY, 1.0, height)) window.y = newY
            }
        } else {
            xOffset = event.sceneX
            yOffset = event.sceneY
            isDragging = true
        }
    }
    private val releaseListener = onPersistPosition?.let { onPersist ->
        EventHandler<MouseEvent> {
            if (isDragging) {
                isDragging = false
                stage.scene?.apply { onPersist(window.x, window.y) }
            }
        }
    }

    init {
        addListeners(stage.scene)
        stage.sceneProperty().addListener(sceneListener)
    }

    fun stop() {
        stage.sceneProperty().removeListener(sceneListener)
        removeListeners(stage.scene)
    }

    private fun addListeners(scene: Scene?) = scene?.apply {
        onMouseDragged = dragListener
        onMouseReleased = releaseListener
    }

    private fun removeListeners(scene: Scene?) = scene?.apply {
        onMouseDragged = null
        onMouseReleased = null
    }

}

