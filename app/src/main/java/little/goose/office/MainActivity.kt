package little.goose.office

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.metrics.performance.JankStats
import dagger.hilt.android.AndroidEntryPoint
import little.goose.design.system.theme.AccountTheme
import little.goose.ui.screen.LittleGooseEmptyScreen
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var lazyStats: dagger.Lazy<JankStats>

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        splashScreen.setKeepOnScreenCondition { !isAppInit }
        super.onCreate(savedInstanceState)

        setContent {
            val viewModel = hiltViewModel<MainViewModel>()
            val appState: AppState by viewModel.appState.collectAsState()
            AccountTheme(appState.themeConfig) {
                when (appState) {
                    is AppState.Loading -> {
                        LittleGooseEmptyScreen(modifier = Modifier.fillMaxSize())
                    }

                    is AppState.Success -> {
                        MainScreen(
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        lazyStats.get().isTrackingEnabled = true
    }

    override fun onPause() {
        super.onPause()
        lazyStats.get().isTrackingEnabled = false
    }
}