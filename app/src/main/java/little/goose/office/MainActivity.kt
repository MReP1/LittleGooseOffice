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

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        splashScreen.setKeepOnScreenCondition { !isAppInit }
        super.onCreate(savedInstanceState)
        setContent {
            AccountTheme {
                val initPage by produceState(initialValue = HOME) {
                    value = homeDataStore.getDataOrDefault(KEY_PREF_PAGER, HOME)
                }
                HomeScreen(
                    modifier = Modifier.fillMaxSize(),
                    initPage = initPage
                )
            }
        }
    }

}