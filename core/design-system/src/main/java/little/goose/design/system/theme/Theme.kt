package little.goose.design.system.theme

import android.app.Activity
import android.content.Context
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.doOnAttach
import kotlin.math.abs

private val DarkColorScheme = darkColorScheme(
)

private val LightColorScheme = lightColorScheme(
)

// TODO 等架构成熟，放到架构中去
var globalThemeConfig by mutableStateOf(ThemeConfig())

@Stable
data class ThemeConfig(
    val isDynamicColor: Boolean = true,
    val themeType: ThemeType = ThemeType.FollowSystem
) {
    @Composable
    fun isDarkTheme() = when (themeType) {
        ThemeType.Light -> false
        ThemeType.Dart -> true
        ThemeType.FollowSystem -> isSystemInDarkTheme()
    }

    @Composable
    fun getColorScheme(context: Context): ColorScheme {
        return if (isDynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            when (themeType) {
                ThemeType.Light -> dynamicLightColorScheme(context)
                ThemeType.Dart -> dynamicDarkColorScheme(context)
                ThemeType.FollowSystem -> if (isSystemInDarkTheme()) {
                    dynamicDarkColorScheme(context)
                } else {
                    dynamicLightColorScheme(context)
                }
            }
        } else {
            when (themeType) {
                ThemeType.Light -> LightColorScheme
                ThemeType.Dart -> DarkColorScheme
                ThemeType.FollowSystem -> if (isSystemInDarkTheme()) {
                    DarkColorScheme
                } else {
                    LightColorScheme
                }
            }
        }
    }
}

enum class ThemeType {
    Light, Dart, FollowSystem
}

@Composable
fun AccountTheme(
    themeConfig: ThemeConfig = globalThemeConfig,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val view = LocalView.current
    val window = (view.context as? Activity)?.window
    val isLightTheme = !themeConfig.isDarkTheme()
    DisposableEffect(view, window) {
        if (window != null) {
            window.statusBarColor = android.graphics.Color.TRANSPARENT
            window.navigationBarColor = android.graphics.Color.TRANSPARENT
            WindowCompat.setDecorFitsSystemWindows(window, false)
        }
        onDispose { }
    }
    DisposableEffect(isLightTheme, view, window) {
        if (window != null) {
            view.doOnAttach {
                WindowCompat.getInsetsController(window, view).apply {
                    isAppearanceLightStatusBars = isLightTheme
                    isAppearanceLightNavigationBars = isLightTheme
                }
            }
        }
        onDispose { }
    }
    MaterialTheme(
        colorScheme = themeConfig.getColorScheme(context = context),
        typography = Typography,
        content = content
    )
}

@Composable
fun WithTransparentStyle(
    modifier: Modifier = Modifier,
    isLightTheme: Boolean = true,
    content: @Composable ColumnScope.() -> Unit
) {
    val view = LocalView.current
    val density: Density = LocalDensity.current
    val window = (view.context as? Activity)?.window
    var statusHeight by remember { mutableStateOf(0.dp) }
    var navHeight by remember { mutableStateOf(0.dp) }
    DisposableEffect(view, density, window) {
        if (window != null) {
            window.statusBarColor = android.graphics.Color.TRANSPARENT
            window.navigationBarColor = android.graphics.Color.TRANSPARENT
            WindowCompat.setDecorFitsSystemWindows(window, false)
            view.doOnAttach {
                ViewCompat.getRootWindowInsets(window.decorView)?.let { rootWindowInsert ->
                    statusHeight = rootWindowInsert.getInsets(WindowInsetsCompat.Type.statusBars())
                        .let { statusInsert ->
                            density.run { (abs(statusInsert.top - statusInsert.bottom)).toDp() }
                        }
                    navHeight = rootWindowInsert.getInsets(WindowInsetsCompat.Type.navigationBars())
                        .let { navInsert ->
                            density.run { (abs(navInsert.top - navInsert.bottom)).toDp() }
                        }
                }
            }
        }
        onDispose { }
    }
    DisposableEffect(isLightTheme, view, window) {
        if (window != null) {
            view.doOnAttach {
                WindowCompat.getInsetsController(window, view).apply {
                    isAppearanceLightStatusBars = isLightTheme
                    isAppearanceLightNavigationBars = isLightTheme
                }
            }
        }
        onDispose { }
    }
    Surface(modifier = modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            Spacer(modifier = Modifier.height(statusHeight))
            content()
            Spacer(modifier = Modifier.height(navHeight))
        }
    }
}