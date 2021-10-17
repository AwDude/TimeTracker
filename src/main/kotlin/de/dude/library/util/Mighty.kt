package de.dude.library.util

import sun.misc.Unsafe
import java.lang.reflect.AccessibleObject
import java.lang.reflect.Field
import java.lang.reflect.Modifier
import kotlin.reflect.KClass
import kotlin.reflect.full.NoSuchPropertyException

class Mighty private constructor(private val obj: Any?, val clazz: Class<*>) {

    companion object {
        val unsafe by lazy { obtainUnsafe() }

        fun ofClass(clazz: KClass<*>) = Mighty(null, clazz.java)

        fun ofClass(clazz: Class<*>) = Mighty(null, clazz)

        fun ofClass(className: String) = Mighty(null, Class.forName(className))

        fun ofObject(obj: Any) = Mighty(obj, obj::class.java)

        private fun obtainUnsafe(): Unsafe {
            val field = Unsafe::class.java.getDeclaredField("theUnsafe")
            field.isAccessible = true
            return field[null] as Unsafe
        }
    }

    @JvmName("invokeUntyped")
    fun invoke(methodName: String, vararg parameters: Any?): Any? {
        val types = parameters.map { it?.let{it::class.java} ?: Any::class.java }.toTypedArray()
        return findNested { getDeclaredMethod(methodName, *types) }.invoke(obj, *parameters)
    }

    @Suppress("UNCHECKED_CAST")
    fun <T> invoke(methodName: String, vararg parameters: Any?) = invoke(methodName, *parameters) as T

    fun <T> get(fieldName: String) = findNested { getDeclaredField(fieldName) }.getAs<T>(obj)

    fun set(fieldName: String, value: Any?) {
        val field = clazz.getDeclaredField(fieldName)
        val isFinal = Modifier.isFinal(field.modifiers)

        if (isFinal && Modifier.isStatic(field.modifiers)) {
            setDirtyStatic(field, value)
            return
        }

        if (!(isFinal || clazz.isHidden || clazz.isRecord)) {
            try {
                field.isAccessible = true
                field.set(obj, value)
                return
            } catch (_: Exception) {
            }
        }
        setDirty(field, value)
    }

    private fun <T : AccessibleObject> findNested(query: Class<*>.() -> T) =
        findNested(clazz, query) ?: throw NoSuchPropertyException()

    private fun <T : AccessibleObject> findNested(clazz: Class<*>, query: Class<*>.() -> T): T? {
        return tryDo { clazz.query().apply { isAccessible = true } }
            ?: clazz.superclass?.let { findNested(it, query) }
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