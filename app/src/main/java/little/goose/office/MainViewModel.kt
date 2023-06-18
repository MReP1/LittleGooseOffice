package little.goose.office

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import little.goose.home.logic.HomePageDataHolder
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    appDataHolder: AppDataHolder,
    homePageDataHolder: HomePageDataHolder
) : ViewModel() {

    val appState = combine(
        appDataHolder.designSystemDataHolder.themeConfig,
        homePageDataHolder.homePage
    ) { themeConfig, homePage ->
        // 预加载 HomePage
        if (homePage != -1) {
            AppState.Success(themeConfig)
        } else {
            AppState.Loading
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, AppState.Loading)

}