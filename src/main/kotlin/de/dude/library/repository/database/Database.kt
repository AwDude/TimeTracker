package de.dude.library.repository.database

object Database {

    fun add(entity: Entity): Int {
        println(entity::class.qualifiedName)
        return -1
    }

    fun delete(entity: Entity) {

    }

    fun <T : Entity> load(id: Int): T? {
        return null
    }

}