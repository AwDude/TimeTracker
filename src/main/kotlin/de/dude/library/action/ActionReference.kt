package de.dude.library.action

import java.lang.ref.WeakReference

class ActionReference<T>(referent: T) : WeakReference<T>(referent) {

    override fun equals(other: Any?) = get() != null && other is WeakReference<*> && this.get() == other.get()

    override fun hashCode() = get()?.hashCode() ?: javaClass.hashCode()

}