package little.goose.office

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    appDataHolder: AppDataHolder
) : ViewModel() {

    val appState = appDataHolder.designSystemDataHolder.themeConfig.map {
        AppState.Success(it)
    }.stateIn(viewModelScope, SharingStarted.Eagerly, AppState.Loading)

}