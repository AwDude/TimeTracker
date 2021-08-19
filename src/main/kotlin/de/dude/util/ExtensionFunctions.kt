package de.dude.util

import de.dude.repository.Settings
import javafx.application.Platform
import javafx.scene.Node
import javafx.stage.Screen

fun Collection<String>.contains(other: String, ignoreCase: Boolean) = any { it.equals(other, ignoreCase) }

@Suppress("UNCHECKED_CAST")
@JvmName("putAddSet")
fun <E, K, V : MutableSet<E>> MutableMap<K, V>.add(key: K, element: E) =
    putIfAbsent(key, mutableSetOf(element) as V)?.add(element)

@Suppress("UNCHECKED_CAST")
@JvmName("putAddList")
fun <E, K, V : MutableList<E>> MutableMap<K, V>.add(key: K, element: E) =
    putIfAbsent(key, mutableListOf(element) as V)?.add(element)

fun runOnUI(runnable: Runnable) = if (Platform.isFxApplicationThread()) runnable.run() else Platform.runLater(runnable)

fun catchAll(run: Runnable, onCatch: ((Exception) -> Unit)? = null) = try {
    run.run()
} catch (e: Exception) {
    onCatch?.invoke(e)
}

fun Node.canDragWindow() {
    var xOffset = 0.0
    var yOffset = 0.0
    var isDragging = false

    setOnMouseDragged { event ->
        scene ?: return@setOnMouseDragged
        if (isDragging) {
            val newX = event.screenX - xOffset
            val newY = event.screenY - yOffset
            if (isInScreen(newX, event.screenY, scene.width, 1.0)) scene.window.x = newX
            if (isInScreen(event.screenX, newY, 1.0, scene.height)) scene.window.y = newY
        } else {
            xOffset = event.sceneX
            yOffset = event.sceneY
            isDragging = true
        }
    }

    setOnMouseReleased {
        scene ?: return@setOnMouseReleased
        if (isDragging) {
            isDragging = false
            Settings.stageX = scene.window.x
            Settings.stageY = scene.window.y
        }
    }
}

fun isInScreen(x: Double, y: Double, width: Double = 1.0, height: Double = 1.0): Boolean {
    Screen.getScreens().forEach { screen ->
        if (screen.visualBounds.contains(x, y, width, height)) return true
    }
    return false
}

fun getTaskBarHeight() = Screen.getPrimary().bounds.height - Screen.getPrimary().visualBounds.height