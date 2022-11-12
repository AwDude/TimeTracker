package de.dude.library.repository.database.entity

import de.dude.library.repository.database.memory.Mapper

abstract class Entity {
    @Suppress("LeakingThis")
    val id: Long = Mapper.add(this)

    override fun equals(other: Any?): Boolean {
        return other is Entity && other.id == this.id
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}