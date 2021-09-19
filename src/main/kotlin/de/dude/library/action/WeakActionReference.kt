package de.dude.library.action

import java.lang.ref.WeakReference

internal class WeakActionReference<T>(referent: T) : ActionReference<T>, WeakReference<T>(referent) {

    override fun equals(other: Any?) = other is ActionReference<*> && this.get() === other.get()

    override fun hashCode() = get()?.hashCode() ?: javaClass.hashCode()

}