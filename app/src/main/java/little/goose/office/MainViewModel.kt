package little.goose.office

import android.app.Application
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import little.goose.common.constants.KEY_HOME_PAGE
import little.goose.home.utils.KEY_PREF_PAGER
import little.goose.home.utils.homeDataStore
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val application: Application,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    val initHomePage = savedStateHandle.getStateFlow(KEY_HOME_PAGE, -1)

    init {
        if (initHomePage.value == -1) {
            viewModelScope.launch {
                savedStateHandle[KEY_HOME_PAGE] =
                    application.homeDataStore.data.first()[KEY_PREF_PAGER] ?: 0
            }
        }
    }

}