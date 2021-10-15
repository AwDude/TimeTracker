package de.dude.library.repository.settings

internal object LibStore : Storage() {

    private enum class Key {
        STAGE_X,
        STAGE_Y,
        STAGE_WIDTH,
        STAGE_HEIGHT;
    }

    fun setStageX(key: String, value: Double?) = setDouble(key + Key.STAGE_X, value)
    fun getStageX(key: String) = getDouble(key + Key.STAGE_X)

    fun setStageY(key: String, value: Double?) = setDouble(key + Key.STAGE_Y, value)
    fun getStageY(key: String) = getDouble(key + Key.STAGE_Y)

    fun setStageWidth(key: String, value: Double?) = setDouble(key + Key.STAGE_WIDTH, value)
    fun getStageWidth(key: String) = getDouble(key + Key.STAGE_WIDTH)

    fun setStageHeight(key: String, value: Double?) = setDouble(key + Key.STAGE_HEIGHT, value)
    fun getStageHeight(key: String) = getDouble(key + Key.STAGE_HEIGHT)

    private fun getDouble(key: Key) = getDouble(key.name)
    private fun setDouble(key: Key, value: Double?) = setDouble(key.name, value)

}