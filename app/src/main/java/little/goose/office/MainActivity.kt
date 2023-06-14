package little.goose.office

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import dagger.hilt.android.AndroidEntryPoint
import little.goose.common.constants.KEY_HOME_PAGE
import little.goose.design.system.theme.AccountTheme
import little.goose.ui.screen.LittleGooseEmptyScreen

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val viewModel by viewModels<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        splashScreen.setKeepOnScreenCondition {
            !isAppInit && viewModel.homePage.value != -1
        }
        super.onCreate(savedInstanceState)
        setContent {
            AccountTheme {
                val homePage by viewModel.homePage.collectAsState()
                if (homePage == -1) {
                    LittleGooseEmptyScreen(
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    MainScreen(
                        modifier = Modifier.fillMaxSize(),
                        homePage = homePage,
                        onHomePageUpdate = viewModel::updateHomePage
                    )
                }
            }
        }
    }

    // I need MissingSuperCall to remove code's wrong, but why?
    @SuppressLint("MissingSuperCall")
    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        updateHomePage(intent)
    }

    private fun updateHomePage(intent: Intent?) {
        if (intent != null) {
            val page = intent.getIntExtra(KEY_HOME_PAGE, -1)
            if (page != -1) {
                viewModel.updateHomePage(page)
            }
        }
    }

}