package de.dude.library.repository

import java.util.prefs.Preferences


@Suppress("MemberVisibilityCanBePrivate")
open class Storage {

    private val prefs: Preferences by lazy { Preferences.userNodeForPackage(this.javaClass) }
    private val localKey: String by lazy { this::class.qualifiedName ?: "" }

    protected fun getString(key: String): String? = prefs.get(key.unique('S'), null)

    protected fun setString(key: String, value: String?) =
        value?.let { prefs.put(key.unique('S'), it) } ?: prefs.remove(key.unique('S'))

    protected fun getDouble(key: String): Double? {
        val value = prefs.getDouble(key.unique('D'), Double.NaN)
        return if (value.isNaN()) null else value
    }

    protected fun setDouble(key: String, value: Double?) =
        value?.let { prefs.putDouble(key.unique('D'), it) } ?: prefs.remove(key.unique('D'))

    protected fun getInt(key: String): Int? = try {
        prefs.get(key.unique('I'), null)?.toInt()
    } catch (e: NumberFormatException) {
        null
    }

    protected fun setInt(key: String, value: Int?) =
        value?.let { prefs.put(key.unique('I'), it.toString()) } ?: prefs.remove(key.unique('I'))

    private fun String.unique(prefix: Char) = "$prefix$localKey$this"

}