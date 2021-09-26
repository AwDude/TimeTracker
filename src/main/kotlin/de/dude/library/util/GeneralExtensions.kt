@file:Suppress("unused")

package de.dude.library.util

inline val Any?.className: String get() = this?.let { it::class.simpleName } ?: "null"

inline val Any?.void: Unit get() = Unit

fun <R> tryDo(run: () -> R): R? = try {
    run()
} catch (e: Exception) {
    null
}

fun noneNull(vararg objects: Any?) = !objects.any { it == null }