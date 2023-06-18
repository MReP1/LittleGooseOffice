package little.goose.home.logic

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import little.goose.home.di.KEY_PREF_PAGER

class HomePageDataHolder(
    private val homeDataStore: DataStore<Preferences>,
    coroutineScope: CoroutineScope
) {

    val homePage = homeDataStore.data.map {
        it[KEY_PREF_PAGER] ?: 0
    }.stateIn(coroutineScope, SharingStarted.Eagerly, -1)

    suspend fun setHomePage(int: Int) {
        homeDataStore.edit {
            it[KEY_PREF_PAGER] = int
        }
    }

}