package little.goose.home.utils

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore

val Context.homeDataStore: DataStore<Preferences> by preferencesDataStore("home")
val KEY_PREF_PAGER = intPreferencesKey("pager")