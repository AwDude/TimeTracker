@file:Suppress("unused")

package de.dude.util.extension

fun <R> tryDo(run: () -> R): R? = try {
    run()
} catch (e: Exception) {
    null
}

val Any?.className: String get() = this?.let { it::class.simpleName } ?: "null"