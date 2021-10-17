package de.dude.library.util

import java.lang.reflect.Field
import java.lang.reflect.Method
import kotlin.reflect.KClass
import kotlin.reflect.KProperty
import kotlin.reflect.jvm.javaField

fun KProperty<*>.isDelegate(delegateClass: KClass<*>) = javaField?.let {
    it.isAccessible = true
    delegateClass.java.isAssignableFrom(it.type)
} ?: false

val KClass<*>.isInterface get() = java.isInterface

@Suppress("UNCHECKED_CAST")
fun <T> Field.getAs(fieldOwner: Any?): T = get(fieldOwner) as T

@Suppress("UNCHECKED_CAST")
fun <T> Method.invokeAs(methodOwner: Any?, vararg parameters: Any?): T = invoke(methodOwner, *parameters) as T