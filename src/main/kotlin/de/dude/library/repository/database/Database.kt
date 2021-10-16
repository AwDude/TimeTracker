package de.dude.library.repository.database

import de.dude.library.repository.database.entity.Entity

object Database {

    fun <T : Entity> get(id: Int): T? {
        return null
    }

    fun delete(entity: Entity) {

    }

}