package de.dude.library.action

interface ActionReference<T> {

    fun get(): T?

    override fun equals(other: Any?): Boolean

    override fun hashCode(): Int

}