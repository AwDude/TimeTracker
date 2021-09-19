package de.dude.library.action

internal interface ActionReference<T> {

    fun get(): T?

    override fun equals(other: Any?): Boolean

    override fun hashCode(): Int

}