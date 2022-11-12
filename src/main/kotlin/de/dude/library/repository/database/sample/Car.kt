package de.dude.library.repository.database.sample

import de.dude.library.repository.database.entity.Entity
import de.dude.library.repository.database.entity.bool
import de.dude.library.repository.database.entity.int
import de.dude.library.repository.database.entity.nullableBool

open class Car: Entity() {
    var b by bool
    var nb by nullableBool
    open var i by int

    private fun hello(i: Any?): Int {
        println("hello Any?")
        return 1
    }

    private fun hello(i: Int?): Int {
        println("hello Int?")
        return 2
    }

    private fun hello(i: Int): Int {
        println("hello Int")
        return 3
    }
}