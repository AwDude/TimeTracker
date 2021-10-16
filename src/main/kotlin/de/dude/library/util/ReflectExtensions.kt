package de.dude.library.util

import kotlin.reflect.KClass
import kotlin.reflect.KProperty
import kotlin.reflect.jvm.javaField

fun KProperty<*>.isDelegate(delegateClass: KClass<*>) = javaField?.let {
    it.isAccessible = true
    delegateClass.java.isAssignableFrom(it.type)
} ?: false

val KClass<*>.isInterface get() = java.isInterface