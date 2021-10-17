package de.dude.library.repository.database.entity

import de.dude.library.repository.database.MemoryMap

private const val ENTITY_FILE_EXTENSION = ".entity"

class EntityMap(entityName: String, private val entitySize: Int) {

    private val memoryMap = MemoryMap(entityName + ENTITY_FILE_EXTENSION)


}