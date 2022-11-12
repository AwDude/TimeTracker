package de.dude.library.repository.database

import de.dude.library.repository.database.entity.Entity
import sun.misc.Unsafe
import kotlin.reflect.KClass

class Database private constructor(path: String) {

    companion object {
        fun create(path: String) { }
        fun open(path: String) { }
        fun openOrCreate(path: String) { }
    }

    init {

    }

    fun transaction() {

    }

    fun <T : Entity> get(id: Int): T? {
        return null
    }

    fun delete(entity: Entity) {

    }

}