package little.goose.home.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import little.goose.common.utils.DebounceActionChannel
import little.goose.home.logic.HomePageDataHolder
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val homePageDataHolder: HomePageDataHolder
) : ViewModel() {

    val dataStoreHomePage get() = homePageDataHolder.homePage.value.takeIf { it != -1 } ?: 0

    private val updateHomePageChannel = DebounceActionChannel(
        coroutineScope = viewModelScope,
        debounceTimeMillis = 1000L,
        action = homePageDataHolder::setHomePage
    )

    fun updateHomePage(homePage: Int) {
        updateHomePageChannel.trySend(homePage)
    }

}