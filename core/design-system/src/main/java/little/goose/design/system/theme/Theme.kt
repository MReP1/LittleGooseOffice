package little.goose.design.system.theme

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.os.Build
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.doOnAttach
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import little.goose.common.utils.getDataOrNull
import kotlin.math.abs

private val gooseLightColorScheme = lightColorScheme(
    primary = md_theme_light_primary,
    onPrimary = md_theme_light_onPrimary,
    primaryContainer = md_theme_light_primaryContainer,
    onPrimaryContainer = md_theme_light_onPrimaryContainer,
    secondary = md_theme_light_secondary,
    onSecondary = md_theme_light_onSecondary,
    secondaryContainer = md_theme_light_secondaryContainer,
    onSecondaryContainer = md_theme_light_onSecondaryContainer,
    tertiary = md_theme_light_tertiary,
    onTertiary = md_theme_light_onTertiary,
    tertiaryContainer = md_theme_light_tertiaryContainer,
    onTertiaryContainer = md_theme_light_onTertiaryContainer,
    error = md_theme_light_error,
    errorContainer = md_theme_light_errorContainer,
    onError = md_theme_light_onError,
    onErrorContainer = md_theme_light_onErrorContainer,
    background = md_theme_light_background,
    onBackground = md_theme_light_onBackground,
    outline = md_theme_light_outline,
    inverseOnSurface = md_theme_light_inverseOnSurface,
    inverseSurface = md_theme_light_inverseSurface,
    inversePrimary = md_theme_light_inversePrimary,
    surfaceTint = md_theme_light_surfaceTint,
    outlineVariant = md_theme_light_outlineVariant,
    scrim = md_theme_light_scrim,
    surface = md_theme_light_surface,
    onSurface = md_theme_light_onSurface,
    surfaceVariant = md_theme_light_surfaceVariant,
    onSurfaceVariant = md_theme_light_onSurfaceVariant,
)

private val gooseDarkColorScheme = darkColorScheme(
    primary = md_theme_dark_primary,
    onPrimary = md_theme_dark_onPrimary,
    primaryContainer = md_theme_dark_primaryContainer,
    onPrimaryContainer = md_theme_dark_onPrimaryContainer,
    secondary = md_theme_dark_secondary,
    onSecondary = md_theme_dark_onSecondary,
    secondaryContainer = md_theme_dark_secondaryContainer,
    onSecondaryContainer = md_theme_dark_onSecondaryContainer,
    tertiary = md_theme_dark_tertiary,
    onTertiary = md_theme_dark_onTertiary,
    tertiaryContainer = md_theme_dark_tertiaryContainer,
    onTertiaryContainer = md_theme_dark_onTertiaryContainer,
    error = md_theme_dark_error,
    errorContainer = md_theme_dark_errorContainer,
    onError = md_theme_dark_onError,
    onErrorContainer = md_theme_dark_onErrorContainer,
    background = md_theme_dark_background,
    onBackground = md_theme_dark_onBackground,
    outline = md_theme_dark_outline,
    inverseOnSurface = md_theme_dark_inverseOnSurface,
    inverseSurface = md_theme_dark_inverseSurface,
    inversePrimary = md_theme_dark_inversePrimary,
    surfaceTint = md_theme_dark_surfaceTint,
    outlineVariant = md_theme_dark_outlineVariant,
    scrim = md_theme_dark_scrim,
    surface = md_theme_dark_surface,
    onSurface = md_theme_dark_onSurface,
    surfaceVariant = md_theme_dark_surfaceVariant,
    onSurfaceVariant = md_theme_dark_onSurfaceVariant,
)

@Stable
data class ThemeConfig(
    val isDynamicColor: Boolean = true,
    val themeType: ThemeType = ThemeType.FOLLOW_SYSTEM
) {
    @Composable
    fun isDarkTheme() = when (themeType) {
        ThemeType.LIGHT -> false
        ThemeType.DART -> true
        ThemeType.FOLLOW_SYSTEM -> isSystemInDarkTheme()
    }

    @Composable
    fun getColorScheme(context: Context): ColorScheme {
        return if (isDynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            when (themeType) {
                ThemeType.LIGHT -> dynamicLightColorScheme(context)
                ThemeType.DART -> dynamicDarkColorScheme(context)
                ThemeType.FOLLOW_SYSTEM -> if (isSystemInDarkTheme()) {
                    dynamicDarkColorScheme(context)
                } else {
                    dynamicLightColorScheme(context)
                }
            }
        } else {
            when (themeType) {
                ThemeType.LIGHT -> gooseLightColorScheme
                ThemeType.DART -> gooseDarkColorScheme
                ThemeType.FOLLOW_SYSTEM -> if (isSystemInDarkTheme()) {
                    gooseDarkColorScheme
                } else {
                    gooseLightColorScheme
                }
            }
        }
    }
}

enum class ThemeType {
    LIGHT, DART, FOLLOW_SYSTEM
}

@Composable
fun AccountTheme(
    themeConfig: ThemeConfig = remember { ThemeConfig() },
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val view = LocalView.current
    val window = (view.context as? Activity)?.window
    val isLightTheme = !themeConfig.isDarkTheme()
    LittleGooseStyle()
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

private val Context.gooseStyleDataStore by preferencesDataStore("gooseStyle")

@Stable
object GooseStyle {
    @Stable
    var goose: Boolean by mutableStateOf(false)

    private val KEY = booleanPreferencesKey("key")

    suspend fun checkGoose(context: Context) {
        context.gooseStyleDataStore.getDataOrNull(KEY)?.let { goose = it }
    }

    suspend fun killGoose(context: Context) {
        context.gooseStyleDataStore.edit { it[KEY] = true }
    }
}

@SuppressLint("SetTextI18n")
@Composable
fun LittleGooseStyle() {
    val view = LocalView.current
    val context = LocalContext.current
    val colorScheme = MaterialTheme.colorScheme
    if (!GooseStyle.goose) {
        LaunchedEffect(GooseStyle.goose) {
            GooseStyle.checkGoose(context)
            if (GooseStyle.goose) return@LaunchedEffect
            findCheckStyleContainer(view).addView(
                TextView(context).apply {
                    gravity = Gravity.CENTER
                    setTextColor(colorScheme.primary.toArgb())
                    alpha = 0.76f
                    text = "\u8be5\u5f00\u6e90\u9879\u76ee\u4ec5\u4f9b\u5b66\u4e60\n" +
                            "\u8bf7\u52ff\u7528\u4e8e\u5176\u4ed6\u7528\u9014"
                    tag = "goose"
                },
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            GooseStyle.goose = true
        }
    }
}

fun findCheckStyleContainer(view: View): ViewGroup {
    return if (view.parent is ComposeView) findCheckStyleContainer(view.parent as View)
    else view.parent as? ViewGroup ?: findCheckStyleContainer(view)
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