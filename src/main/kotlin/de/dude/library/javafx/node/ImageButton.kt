package de.dude.library.javafx.node

import javafx.application.Platform
import javafx.beans.binding.Bindings
import javafx.beans.property.SimpleIntegerProperty
import javafx.scene.control.Button
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import kotlin.math.max

private const val DEFAULT_STYLE_CLASS = "image-button"

class ImageButton : Button() {

    private val imageView by lazy {
        ImageView().apply {
            isPreserveRatio = true
            isPickOnBounds = true
        }
    }

    private val imagePaddingProperty by lazy { SimpleIntegerProperty(0) }

    private val fitHeightBinding by lazy {
        Bindings.createDoubleBinding({
            max(height - (padding.top + padding.bottom + (2 * imagePadding)), 1.0)
        }, heightProperty(), paddingProperty(), imagePaddingProperty)
    }

    private val fitWidthBinding by lazy {
        Bindings.createDoubleBinding({
            max(width - (padding.left + padding.right + (2 * imagePadding)), 1.0)
        }, widthProperty(), paddingProperty(), imagePaddingProperty)
    }

    var imagePadding: Int
        get() = imagePaddingProperty.get()
        set(value) = imagePaddingProperty.set(max(value, 0))

    var image: String? = null
        set(value) {
            field = value
            loadImage(value)
        }

    var autoFit: Boolean = true
        set(value) {
            field = value
            bindSize(value)
        }

    var fitWidth: Double
        get() = imageView.fitWidth
        set(value) {
            autoFit = false
            imageView.fitWidth = value
        }

    var fitHeight: Double
        get() = imageView.fitHeight
        set(value) {
            autoFit = false
            imageView.fitHeight = value
        }

    var preserveRatio: Boolean
        get() = imageView.isPreserveRatio
        set(value) {
            imageView.isPreserveRatio = value
        }

    init {
        styleClass.setAll(DEFAULT_STYLE_CLASS)
        Platform.runLater {
            if (autoFit && imageView.fitHeight == 0.0 && imageView.fitWidth == 0.0) {
                autoFit = true
            }
        }
    }

    private fun loadImage(imagePath: String?) = if (imagePath.isNullOrBlank()) {
        graphic = null
    } else {
        imageView.image = Image(imagePath)
        graphic = imageView
    }

    private fun bindSize(fill: Boolean) = if (fill) {
        imageView.fitHeightProperty().bind(fitHeightBinding)
        imageView.fitWidthProperty().bind(fitWidthBinding)
    } else {
        imageView.fitHeightProperty().unbind()
        imageView.fitWidthProperty().unbind()
    }

}