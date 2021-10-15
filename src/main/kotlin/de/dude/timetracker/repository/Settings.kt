package de.dude.timetracker.repository

import de.dude.library.repository.settings.Storage


object Settings : Storage() {

    private enum class Key {
        STAGE_X,
        STAGE_Y,
        STAGE_WIDTH,
        STAGE_HEIGHT;
    }

    var stageX: Double?
        get() = getDouble(Key.STAGE_X)
        set(value) = setDouble(Key.STAGE_X, value)

    var stageY: Double?
        get() = getDouble(Key.STAGE_Y)
        set(value) = setDouble(Key.STAGE_Y, value)

    var stageWidth: Double
        get() = getDouble(Key.STAGE_WIDTH) ?: Defaults.STAGE_WIDTH
        set(value) = setDouble(Key.STAGE_WIDTH, value)

    var stageHeight: Double
        get() = getDouble(Key.STAGE_HEIGHT) ?: Defaults.STAGE_HEIGHT
        set(value) = setDouble(Key.STAGE_HEIGHT, value)

    private fun getDouble(key: Key) = getDouble(key.name)
    private fun setDouble(key: Key, value: Double?) = setDouble(key.name, value)

}