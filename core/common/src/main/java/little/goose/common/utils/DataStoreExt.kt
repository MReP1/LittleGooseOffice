package little.goose.common.utils

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map

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

suspend inline fun DataStore<Preferences>.initial() {
    data.first()
}