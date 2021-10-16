package de.dude.library.repository.database.entity

import de.dude.library.repository.database.Mapper

abstract class Entity {

    @Suppress("LeakingThis")
    val id: Int = Mapper.add(this)

}