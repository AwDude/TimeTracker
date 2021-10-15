package de.dude.library.repository.database

import kotlin.reflect.KProperty

class Attribute<T> {

    private var bla: T? = null

    operator fun getValue(thisRef: Any?, property: KProperty<*>): T? {
        println("$bla has been returned to '${property.name}' in $thisRef.")
        return bla
    }

    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T?) {
        println("$value has been assigned to '${property.name}' in $thisRef.")
        bla = value
    }
}