package dev.achmad.core.network

import dev.achmad.core.DEFAULT_HOST
import dev.achmad.core.NETWORK_PREF
import dev.achmad.core.preference.PreferenceStore

class NetworkPreferences(
    private val preferenceStore: PreferenceStore
) {
    private val prefix: String = NETWORK_PREF

    fun host() = preferenceStore.getString(prefix.plus("host"), DEFAULT_HOST)
    fun cookies() = preferenceStore.getStringSet(prefix.plus("cookies"))

}