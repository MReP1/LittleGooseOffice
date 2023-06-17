package little.goose.office

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.hilt.navigation.compose.hiltViewModel
import dagger.hilt.android.AndroidEntryPoint
import little.goose.design.system.theme.AccountTheme
import little.goose.ui.screen.LittleGooseEmptyScreen

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        splashScreen.setKeepOnScreenCondition { !isAppInit }
        super.onCreate(savedInstanceState)

        if (window != null) {
            window.statusBarColor = android.graphics.Color.TRANSPARENT
            window.navigationBarColor = android.graphics.Color.TRANSPARENT
            WindowCompat.setDecorFitsSystemWindows(window, false)
        }

        setContent {
            val viewModel = hiltViewModel<MainViewModel>()
            val appState by viewModel.appState.collectAsState()
            when (val state = appState) {
                is AppState.Loading -> {
                    LittleGooseEmptyScreen(modifier = Modifier.fillMaxSize())
                }

                is AppState.Success -> {
                    val themeConfig = state.themeConfig
                    AccountTheme(themeConfig) {
                        MainScreen(
                            modifier = Modifier.fillMaxSize(),
                            themeConfig = themeConfig,
                            onThemeConfigChange = viewModel::setThemeConfig
                        )
                    }
                }
            }
        }
    }

}