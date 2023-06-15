package little.goose.home.ui

import android.app.Application
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import little.goose.common.utils.DebounceActionChannel
import little.goose.home.utils.KEY_PREF_PAGER
import little.goose.home.utils.homeDataStore
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    application: Application
): ViewModel() {

    private val updateHomePageChannel = DebounceActionChannel<Int>(
        coroutineScope = viewModelScope,
        debounceTimeMillis = 1000L,
        action = { homePage ->
            application.homeDataStore.edit {
                it[KEY_PREF_PAGER] = homePage
            }
        }
    )

    fun updateHomePage(homePage: Int) {
        updateHomePageChannel.trySend(homePage)
    }

}