package de.dude.library.repository.database

import java.nio.ByteBuffer
import kotlin.reflect.KProperty

interface Attribute<T> {
    operator fun getValue(thisRef: Any?, property: KProperty<*>): T?
    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T?)
}

private class FixedAttribute<T>(caller: Entity, numBytes: Byte) : Attribute<T> {

    init {
        println(caller)
    }

    private var bla: T? = null

    override operator fun getValue(thisRef: Any?, property: KProperty<*>): T? {
        println("$bla has been returned to '${property.name}' in $thisRef.")
        return bla
    }

    override operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T?) {
        println("$value has been assigned to '${property.name}' in $thisRef.")
        bla = value
        Int
    }

}

val Entity.boolean: Attribute<Boolean> get() = FixedAttribute(this, 1)
val Entity.byte: Attribute<Byte> get() = FixedAttribute(this, 1)
val Entity.char: Attribute<Char> get() = FixedAttribute(this, 2)
val Entity.short: Attribute<Short> get() = FixedAttribute(this, 2)
val Entity.int: Attribute<Int> get() = FixedAttribute(this, 4)
val Entity.float: Attribute<Float> get() = FixedAttribute(this, 4)
val Entity.long: Attribute<Long> get() = FixedAttribute(this, 8)
val Entity.double: Attribute<Double> get() = FixedAttribute(this, 8)

/*fun Entity.entity(): Attribute<Entity> =
fun Entity.string(): Attribute<String> =
fun Entity.list(): Attribute<AttributeList> =*/


// char, bool, string, list with listener