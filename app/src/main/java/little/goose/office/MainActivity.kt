package little.goose.office

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import dagger.hilt.android.AndroidEntryPoint
import little.goose.design.system.theme.AccountTheme
import little.goose.home.ui.HomeScreen

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        splashScreen.setKeepOnScreenCondition { !isAppInit }
        super.onCreate(savedInstanceState)
        setContent {
            AccountTheme {
                HomeScreen(modifier = Modifier.fillMaxSize())
            }
        }
    }

}