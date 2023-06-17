package little.goose.office

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import little.goose.design.system.theme.ThemeConfig
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val appStateHolder: AppStateHolder
) : ViewModel() {

    val appState = appStateHolder.designSystemStateHolder.themeConfig.map {
        AppState.Success(it)
    }.stateIn(viewModelScope, SharingStarted.Eagerly, AppState.Loading)

    fun setThemeConfig(themeConfig: ThemeConfig) {
        viewModelScope.launch {
            appStateHolder.designSystemStateHolder.setThemeConfig(themeConfig)
        }
    }

}