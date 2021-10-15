package de.dude.library.util

import sun.misc.Unsafe
import java.lang.reflect.Field
import java.lang.reflect.Modifier

class Mighty private constructor(private val obj: Any?, val clazz: Class<*>) {

    companion object {
        val unsafe by lazy { obtainUnsafe() }

        fun ofClass(clazz: Class<*>) = Mighty(null, clazz)

        fun ofClass(className: String) = Mighty(null, Class.forName(className))

        fun ofObject(obj: Any) = Mighty(obj, obj::class.java)

        private fun obtainUnsafe(): Unsafe {
            val field = Unsafe::class.java.getDeclaredField("theUnsafe")
            field.isAccessible = true
            val unsafe = field[null] as Unsafe
            field.isAccessible = false
            return unsafe
        }
    }

    inline fun <reified T> get(fieldName: String): T? {
        return get(fieldName, T::class.java)
    }

    fun <T> get(fieldName: String, type: Class<T>): T? {
        var clazz: Class<*>? = this.clazz
        do {
            val field = tryDo { clazz!!.getDeclaredField(fieldName) }
            if (field != null && isAssignable(field, type)) {
                @Suppress("UNCHECKED_CAST")
                return get(field) as T?
            }
            clazz = clazz!!.superclass
        } while (clazz != null)
        throw NoSuchFieldException("No field \"$fieldName\" of type \"${type.name}\" in class \"${this.clazz.name}\"")
    }

    private fun isAssignable(field: Field, type: Class<*>): Boolean {
        val fieldType = if (field.type.isPrimitive) field.type.kotlin.javaObjectType else field.type
        return type.isAssignableFrom(fieldType)
    }

    fun get(field: Field): Any? {
        val wasAccessible = field.canAccess(obj)
        field.isAccessible = true
        val value = field.get(obj)
        field.isAccessible = wasAccessible
        return value
    }

    fun set(fieldName: String, value: Any?) {
        val field = clazz.getDeclaredField(fieldName)
        val isFinal = Modifier.isFinal(field.modifiers)

        if (isFinal && Modifier.isStatic(field.modifiers)) {
            setDirtyStatic(field, value)
            return
        }

        if (!(isFinal || clazz.isHidden || clazz.isRecord)) {
            try {
                val wasAccessible = field.canAccess(obj)
                field.isAccessible = true
                field.set(obj, value)
                field.isAccessible = wasAccessible
                return
            } catch (_: Exception) {
            }
        }
        setDirty(field, value)
    }

    private fun setDirty(field: Field, value: Any?) {
        val offset = unsafe.objectFieldOffset(field)
        putDirty(field, obj!!, offset, value)
    }

    private fun setDirtyStatic(field: Field, value: Any?) {
        val base = unsafe.staticFieldBase(field)
        val offset = unsafe.staticFieldOffset(field)
        putDirty(field, base, offset, value)
    }

    private fun putDirty(field: Field, obj: Any, offset: Long, value: Any?) =
        if (Modifier.isVolatile(field.modifiers)) {
            putDirtyVolatile(field.type, obj, offset, value)
        } else {
            putDirty(field.type, obj, offset, value)
        }

    private fun putDirty(type: Class<*>, obj: Any, offset: Long, value: Any?) = when (type) {
        Boolean::class.java -> unsafe.putBoolean(obj, offset, value as Boolean)
        Byte::class.java -> unsafe.putByte(obj, offset, value as Byte)
        Char::class.java -> unsafe.putChar(obj, offset, value as Char)
        Short::class.java -> unsafe.putShort(obj, offset, value as Short)
        Int::class.java -> unsafe.putInt(obj, offset, value as Int)
        Long::class.java -> unsafe.putLong(obj, offset, value as Long)
        Float::class.java -> unsafe.putFloat(obj, offset, value as Float)
        Double::class.java -> unsafe.putDouble(obj, offset, value as Double)
        else -> unsafe.putObject(obj, offset, value)
    }

    private fun putDirtyVolatile(type: Class<*>, obj: Any, offset: Long, value: Any?) = when (type) {
        Boolean::class.java -> unsafe.putBooleanVolatile(obj, offset, value as Boolean)
        Byte::class.java -> unsafe.putByteVolatile(obj, offset, value as Byte)
        Char::class.java -> unsafe.putCharVolatile(obj, offset, value as Char)
        Short::class.java -> unsafe.putShortVolatile(obj, offset, value as Short)
        Int::class.java -> unsafe.putIntVolatile(obj, offset, value as Int)
        Long::class.java -> unsafe.putLongVolatile(obj, offset, value as Long)
        Float::class.java -> unsafe.putFloatVolatile(obj, offset, value as Float)
        Double::class.java -> unsafe.putDoubleVolatile(obj, offset, value as Double)
        else -> unsafe.putObjectVolatile(obj, offset, value)
    }

}