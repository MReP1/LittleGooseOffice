package little.goose.office

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import dagger.hilt.android.AndroidEntryPoint
import little.goose.common.utils.getDataOrDefault
import little.goose.design.system.theme.AccountTheme
import little.goose.home.data.HOME
import little.goose.home.ui.HomeScreen
import little.goose.home.utils.KEY_PREF_PAGER
import little.goose.home.utils.homeDataStore

sealed interface MainState {
    object Loading : MainState
    data class Success(val page: Int) : MainState
}

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    companion object {
        @Volatile
        @JvmStatic
        var isMainPageInit = false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        splashScreen.setKeepOnScreenCondition { !isAppInit && !isMainPageInit }
        super.onCreate(savedInstanceState)
        setContent {
            AccountTheme {
                val mainState = produceState<MainState>(initialValue = MainState.Loading) {
                    val initPage = homeDataStore.getDataOrDefault(KEY_PREF_PAGER, HOME)
                    isMainPageInit = true
                    value = MainState.Success(initPage)
                }
                when (val state = mainState.value) {
                    MainState.Loading -> {
                        // TODO
                    }

                    is MainState.Success -> {
                        HomeScreen(
                            modifier = Modifier.fillMaxSize(),
                            initPage = state.page
                        )
                    }
                }
            }
        }
    }

}