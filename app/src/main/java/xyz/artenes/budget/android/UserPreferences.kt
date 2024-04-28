package xyz.artenes.budget.android

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.CoroutineContext

@Singleton
class UserPreferences @Inject constructor(
    private val dataStore: DataStore<Preferences>,
    private val dispatcher: CoroutineContext
) {

    suspend fun save(key: String, value: String) {
        withContext(dispatcher) {
            dataStore.edit { settings ->
                settings[stringPreferencesKey(key)] = value
            }
        }
    }

    suspend fun get(key: String, default: String = ""): String {
        return withContext(dispatcher) {
            dataStore.data.map { preferences ->
                preferences[stringPreferencesKey(key)] ?: default
            }.first()
        }
    }

    fun listen(key: String, default: String = "") =
        dataStore.data.map { it[stringPreferencesKey(key)] ?: default }

}