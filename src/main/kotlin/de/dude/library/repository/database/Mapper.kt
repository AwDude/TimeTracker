package de.dude.library.repository.database

import de.dude.library.repository.database.entity.Attribute
import de.dude.library.repository.database.entity.Entity
import de.dude.library.repository.database.entity.EntityMap
import de.dude.library.util.isDelegate
import kotlin.reflect.KClass
import kotlin.reflect.KProperty
import kotlin.reflect.KProperty1
import kotlin.reflect.full.memberProperties


object Mapper {

    private val entityMaps = hashMapOf<KClass<out Entity>, EntityMap?>()
    private val attributeOffsets = hashMapOf<KProperty<*>, Int>()

    fun add(entity: Entity): Int {
        getEntityMap(entity::class)?.apply {
            // find free space -> return id
        }
        return -1
    }

/*    fun writeFixedAttribute(entity: Entity, attribute: KProperty<*>, value:) {

    }*/

    private fun getEntityMap(entityClass: KClass<out Entity>) = entityMaps[entityClass] ?: createEntityMap(entityClass)

    private fun createEntityMap(entityClass: KClass<out Entity>): EntityMap? {
        val entitySize = calculateOffsetsAndGetEntitySize(entityClass)
        val entityMap = if (entitySize > 0) EntityMap(entityClass.qualifiedName!!, entitySize) else null
        entityMaps[entityClass] = entityMap
        return entityMap
    }

    private fun calculateOffsetsAndGetEntitySize(entityClass: KClass<out Entity>): Int {
        var size = 0
        entityClass.memberProperties.forEach { property ->
            if (property.isDelegate(Attribute::class)) {
                val numBytes = property.numBytes
                if (numBytes > 0) {
                    attributeOffsets[property] = size
                    size += numBytes
                }
            }
        }
        return size
    }

    private val KProperty1<out Entity, *>.numBytes: Byte
        get() = when (returnType.classifier) {
            Boolean::class -> 1
            Byte::class -> 1
            Char::class -> 2
            Short::class -> 2
            Int::class -> 4
            Float::class -> 4
            Long::class -> 8
            Double::class -> 8
            String::class -> 8
            Entity::class -> 8
            else -> 0
        }
}