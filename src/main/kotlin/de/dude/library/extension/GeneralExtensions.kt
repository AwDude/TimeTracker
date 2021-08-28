@file:Suppress("unused")

package de.dude.library.extension

fun <R> tryDo(run: () -> R): R? = try {
    run()
} catch (e: Exception) {
    null
}

val Any?.className: String get() = this?.let { it::class.simpleName } ?: "null"