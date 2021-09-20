package de.dude.library.javafx.node

import javafx.beans.binding.Bindings
import javafx.beans.property.SimpleIntegerProperty
import javafx.scene.control.Button
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import kotlin.math.max

private const val DEFAULT_STYLE_CLASS = "image-button"

class ImageButton : Button() {

    private val imageView = ImageView().apply {
        isPreserveRatio = true
        isPickOnBounds = true
    }

    private var imagePaddingProperty = SimpleIntegerProperty(0)

    var imagePadding: Int
        get() = imagePaddingProperty.get()
        set(value) = imagePaddingProperty.set(max(value, 0))

    var image: String? = null
        set(value) {
            field = value
            loadImage(value)
        }

    var preserveRatio: Boolean
        get() = imageView.isPreserveRatio
        set(value) {
            imageView.isPreserveRatio = value
        }

    init {
        styleClass.setAll(DEFAULT_STYLE_CLASS)
        bindImageSize()
    }

    private fun bindImageSize() {
        val fitHeightBinding = Bindings.createDoubleBinding({
            max(height - (padding.top + padding.bottom + (2 * imagePadding)), 1.0)
        }, heightProperty(), paddingProperty(), imagePaddingProperty)
        imageView.fitHeightProperty().bind(fitHeightBinding)

        val fitWidthBinding = Bindings.createDoubleBinding({
            max(width - (padding.left + padding.right + (2 * imagePadding)), 1.0)
        }, widthProperty(), paddingProperty(), imagePaddingProperty)
        imageView.fitWidthProperty().bind(fitWidthBinding)
    }

    private fun loadImage(imagePath: String?) = if (imagePath.isNullOrBlank()) {
        graphic = null
    } else {
        imageView.image = Image(imagePath)
        graphic = imageView
    }

}