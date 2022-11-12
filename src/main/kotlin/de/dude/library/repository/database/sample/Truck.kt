package de.dude.library.repository.database.sample

import de.dude.library.repository.database.entity.int
import de.dude.library.repository.database.entity.nullableInt

class Truck: Car() {
    override var i by int
    var ni by nullableInt
}