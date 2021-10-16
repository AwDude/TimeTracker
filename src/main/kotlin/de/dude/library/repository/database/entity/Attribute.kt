package de.dude.library.repository.database.entity

import java.nio.ByteBuffer
import kotlin.reflect.KProperty

interface Attribute<T> {
    operator fun getValue(entity: Entity, property: KProperty<*>): T
    operator fun setValue(entity: Entity, property: KProperty<*>, value: T)
}

interface NullableAttribute<T> {
    operator fun getValue(entity: Entity, property: KProperty<*>): T?
    operator fun setValue(entity: Entity, property: KProperty<*>, value: T?)
}

internal object BoolAttribute : Attribute<Boolean> {
    override operator fun getValue(entity: Entity, property: KProperty<*>): Boolean {
        return false
    }

    override operator fun setValue(entity: Entity, property: KProperty<*>, value: Boolean) {
    }
}

internal object NullableBoolAttribute : Attribute<Boolean?> {
    override operator fun getValue(entity: Entity, property: KProperty<*>): Boolean? {
        return false
    }

    override operator fun setValue(entity: Entity, property: KProperty<*>, value: Boolean?) {
    }
}

internal object IntAttribute : Attribute<Int> {
    override operator fun getValue(entity: Entity, property: KProperty<*>): Int {
        return 0
    }

    override operator fun setValue(entity: Entity, property: KProperty<*>, value: Int) {
    }
}

internal object NullableIntAttribute : Attribute<Int?> {
    override operator fun getValue(entity: Entity, property: KProperty<*>): Int? {
        return 0
    }

    override operator fun setValue(entity: Entity, property: KProperty<*>, value: Int?) {
        val putValue: ByteBuffer.(Int, Int) -> ByteBuffer = ByteBuffer::putInt
    }
}

val Entity.bool: Attribute<Boolean> get() = BoolAttribute
val Entity.nullableBool: Attribute<Boolean?> get() = NullableBoolAttribute

val Entity.int: Attribute<Boolean> get() = BoolAttribute
val Entity.nullableInt: Attribute<Boolean?> get() = NullableBoolAttribute

/*val Entity.byte: Attribute<Byte> get() = FixedAttribute()
val Entity.char: Attribute<Char> get() = FixedAttribute()
val Entity.short: Attribute<Short> get() = FixedAttribute()
val Entity.int: Attribute<Int> get() = FixedAttribute()
val Entity.float: Attribute<Float> get() = FixedAttribute()
val Entity.long: Attribute<Long> get() = FixedAttribute()
val Entity.double: Attribute<Double> get() = FixedAttribute()*/

/*fun Entity.entity: Attribute<Entity> =
fun Entity.string: Attribute<String> =
fun Entity.list: Attribute<AttributeList> =*/