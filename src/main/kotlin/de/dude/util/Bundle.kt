package de.dude.util

import de.dude.repository.Defaults
import java.util.*

object Bundle {
    fun get(key: String): ResourceBundle = try {
        ResourceBundle.getBundle(key)
    } catch (e: MissingResourceException) {
        ResourceBundle.getBundle(key, Defaults.LANGUAGE)
    }
}