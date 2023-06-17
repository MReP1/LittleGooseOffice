package little.goose.settings

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import com.google.accompanist.navigation.animation.composable

const val ROUTE_SETTINGS = "settings"

fun NavController.navigateToSettings() {
    navigate(ROUTE_SETTINGS) {
        launchSingleTop = true
    }
}

fun NavGraphBuilder.settingsRoute(
    onBack: () -> Unit
) {
    composable(ROUTE_SETTINGS) {
        SettingsRoute(onBack = onBack)
    }
}

@Composable
fun SettingsRoute(
    onBack: () -> Unit
) {
    val viewModel = hiltViewModel<SettingsViewModel>()
    val settingsState by viewModel.settingsState.collectAsState()
    SettingsScreen(
        modifier = Modifier.fillMaxSize(),
        settingsState = settingsState,
        onBack = onBack
    )
}