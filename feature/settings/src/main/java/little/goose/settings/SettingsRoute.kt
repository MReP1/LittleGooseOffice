package little.goose.settings

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import com.google.accompanist.navigation.animation.composable
import little.goose.design.system.theme.ThemeType

const val ROUTE_SETTINGS = "settings"

fun NavController.navigateToSettings() {
    navigate(ROUTE_SETTINGS) {
        launchSingleTop = true
    }
}

fun NavGraphBuilder.settingsRoute(
    onBack: () -> Unit,
    isDynamicColor: Boolean,
    onDynamicColorChange: (Boolean) -> Unit,
    themeType: ThemeType,
    onThemeTypeChange: (ThemeType) -> Unit,
) {
    composable(ROUTE_SETTINGS) {
        SettingsRoute(
            onBack = onBack,
            isDynamicColor = isDynamicColor,
            onDynamicColorChange = onDynamicColorChange,
            themeType = themeType,
            onThemeTypeChange = onThemeTypeChange,
        )
    }
}

@Composable
fun SettingsRoute(
    onBack: () -> Unit,
    isDynamicColor: Boolean,
    onDynamicColorChange: (Boolean) -> Unit,
    themeType: ThemeType,
    onThemeTypeChange: (ThemeType) -> Unit,
) {
    val viewModel = hiltViewModel<SettingsViewModel>()
    val settingsState = viewModel.settingsState.collectAsState()
    SettingsScreen(
        modifier = Modifier.fillMaxSize(),
        isDynamicColor = isDynamicColor,
        onDynamicColorChange = onDynamicColorChange,
        themeType = themeType,
        onThemeTypeChange = onThemeTypeChange,
        onBack = onBack
    )
}