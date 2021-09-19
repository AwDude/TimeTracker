@file:Suppress("unused")

package de.dude.library.extension

inline val Collection<*>?.nullableSize: Int get() = this?.size ?: 0

fun Collection<String>.contains(other: String, ignoreCase: Boolean) = any { it.equals(other, ignoreCase) }

@Suppress("UNCHECKED_CAST")
fun <E, K, V : MutableSet<E>> MutableMap<K, V>.addInSet(key: K, element: E) {
    putIfAbsent(key, mutableSetOf(element) as V)?.add(element)
}

@Suppress("UNCHECKED_CAST")
fun <E, K, V : MutableList<E>> MutableMap<K, V>.addInList(key: K, element: E) {
    putIfAbsent(key, mutableListOf(element) as V)?.add(element)
}