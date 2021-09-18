package de.dude.library.action

class StrongActionReference<T>(private val referent: T) : ActionReference<T> {

    override fun get(): T? = referent

    override fun equals(other: Any?) = other is ActionReference<*> && this.get() === other.get()

    override fun hashCode() = get()?.hashCode() ?: javaClass.hashCode()

}