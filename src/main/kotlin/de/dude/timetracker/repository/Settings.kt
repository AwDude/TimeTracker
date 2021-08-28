package de.dude.timetracker.repository

import java.util.prefs.Preferences


object Settings {

    private enum class Key {
        STAGE_X,
        STAGE_Y,
        STAGE_WIDTH,
        STAGE_HEIGHT;
    }

    private val prefs = Preferences.userNodeForPackage(this.javaClass)

/*    var dbHost: String
        get() = prefs.get(Key.DB_HOST.name, "")
        set(value) = prefs.put(Key.DB_HOST.name, value)*/

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

    private fun getDouble(key: Key): Double? {
        val value = prefs.getDouble(key.name, Double.NaN)
        return if (value.isNaN()) null else value
    }

    private fun setDouble(key: Key, value: Double?) {
        value?.let { prefs.putDouble(key.name, it) }
    }

}