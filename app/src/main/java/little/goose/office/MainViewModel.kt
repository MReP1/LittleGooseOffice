package little.goose.office

import android.app.Application
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import little.goose.common.constants.KEY_HOME_PAGE
import little.goose.common.utils.DebounceActionChannel
import little.goose.home.utils.KEY_PREF_PAGER
import little.goose.home.utils.homeDataStore
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val application: Application,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    val homePage = savedStateHandle.getStateFlow(KEY_HOME_PAGE, -1)

    init {
        if (homePage.value == -1) {
            viewModelScope.launch {
                savedStateHandle[KEY_HOME_PAGE] =
                    application.homeDataStore.data.first()[KEY_PREF_PAGER] ?: 0
            }
        }
    }

    private val updateHomePageChannel = DebounceActionChannel<Int>(
        coroutineScope = viewModelScope, debounceTime = 1000L
    ) { page ->
        application.homeDataStore.edit { preferences ->
            preferences[KEY_PREF_PAGER] = page
        }
    }

    fun updateHomePage(page: Int) {
        savedStateHandle[KEY_HOME_PAGE] = page
        updateHomePageChannel.trySend(page)
    }
}