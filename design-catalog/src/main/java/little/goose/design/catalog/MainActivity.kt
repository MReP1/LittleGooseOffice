package little.goose.design.catalog

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import little.goose.design.catalog.ui.MainScreen
import little.goose.design.system.theme.GooseTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GooseTheme {
                MainScreen()
            }
        }
    }
}