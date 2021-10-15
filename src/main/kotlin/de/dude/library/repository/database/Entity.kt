package de.dude.library.repository.database

open class Entity {

    val id: Int = -1

    var name by Attribute<String>()

    init {
        name = "lololol"

        val i = name
    }

}