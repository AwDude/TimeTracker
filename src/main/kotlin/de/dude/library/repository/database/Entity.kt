package de.dude.library.repository.database

abstract class Entity {

    val id: Int = Database.add(this)

}