package little.goose.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import little.goose.design.system.state.DesignSystemDataHolder
import little.goose.design.system.theme.ThemeConfig
import little.goose.design.system.theme.ThemeType
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val designSystemDataHolder: DesignSystemDataHolder
) : ViewModel() {

    val settingsState: StateFlow<SettingsState> =
        designSystemDataHolder.themeConfig.map { themeConfig ->
            SettingsState.Success(
                isDynamicColor = themeConfig.isDynamicColor,
                themeType = themeConfig.themeType,
                onThemeTypeChange = ::setThemeType,
                onDynamicColorChange = ::setIsDynamicColor
            )
        }.stateIn(viewModelScope, SharingStarted.Eagerly, SettingsState.Loading)

    private fun setIsDynamicColor(isDynamicColor: Boolean) {
        (settingsState.value as? SettingsState.Success)?.let {
            setThemeConfig(ThemeConfig(isDynamicColor = isDynamicColor, themeType = it.themeType))
        }
    }

    private fun setThemeType(themeType: ThemeType) {
        (settingsState.value as? SettingsState.Success)?.let {
            setThemeConfig(ThemeConfig(isDynamicColor = it.isDynamicColor, themeType = themeType))
        }
    }

    private fun setThemeConfig(themeConfig: ThemeConfig) {
        viewModelScope.launch {
            designSystemDataHolder.setThemeConfig(themeConfig)
        }
    }

}