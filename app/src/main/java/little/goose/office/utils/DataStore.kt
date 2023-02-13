package little.goose.office.utils

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.*
import little.goose.office.appContext

val Context.homeDataStore: DataStore<Preferences> by preferencesDataStore("home")
inline val homeDataStore get() = appContext.homeDataStore
val KEY_PREF_PAGER = intPreferencesKey("pager")

fun <T> DataStore<Preferences>.flowDataOrNull(
    key: Preferences.Key<T>
): Flow<T?> = this.data.map { preferences ->
    preferences[key]
}

fun <T> DataStore<Preferences>.flowDataOrDefault(
    key: Preferences.Key<T>, default: T
): Flow<T> = this.data.map { preferences ->
    preferences[key] ?: default
}

suspend inline fun <T> DataStore<Preferences>.getDataOrNull(
    key: Preferences.Key<T>
): T? = this.data.firstOrNull()?.get(key)

suspend inline fun <T> DataStore<Preferences>.getDataOrDefault(
    key: Preferences.Key<T>, default: T
): T = this.data.firstOrNull()?.get(key) ?: default

suspend inline fun <T> DataStore<Preferences>.withData(
    key: Preferences.Key<T>, action: (T) -> Unit
) = this.getDataOrNull(key)?.also { action(it) }

suspend inline fun DataStore<Preferences>.withPref(
    action: (Preferences) -> Unit
) {
    this.data.firstOrNull()?.also { action(it) }
}

enum class DataStoreHelper {
    INSTANCE; //封装一层是因为不想将初始化方法暴露到顶层

    suspend fun initDataStore() {
        homeDataStore.data.first()
    }
}