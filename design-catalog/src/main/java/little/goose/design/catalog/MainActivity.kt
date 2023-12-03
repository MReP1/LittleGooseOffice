package little.goose.design.catalog

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import little.goose.design.catalog.ui.ChartCatalogScreen
import little.goose.design.system.theme.AccountTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AccountTheme {
                ChartCatalogScreen(modifier = Modifier.fillMaxSize())
            }
        }
    }
}