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
import androidx.compose.material3.*
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import little.goose.common.utils.getDataOrNull

private val gooseLightColorScheme = lightColorScheme(
    primary = primaryLight,
    onPrimary = onPrimaryLight,
    primaryContainer = primaryContainerLight,
    onPrimaryContainer = onPrimaryContainerLight,
    secondary = secondaryLight,
    onSecondary = onSecondaryLight,
    secondaryContainer = secondaryContainerLight,
    onSecondaryContainer = onSecondaryContainerLight,
    tertiary = tertiaryLight,
    onTertiary = onTertiaryLight,
    tertiaryContainer = tertiaryContainerLight,
    onTertiaryContainer = onTertiaryContainerLight,
    error = errorLight,
    onError = onErrorLight,
    errorContainer = errorContainerLight,
    onErrorContainer = onErrorContainerLight,
    background = backgroundLight,
    onBackground = onBackgroundLight,
    surface = surfaceLight,
    onSurface = onSurfaceLight,
    surfaceVariant = surfaceVariantLight,
    onSurfaceVariant = onSurfaceVariantLight,
    outline = outlineLight,
    outlineVariant = outlineVariantLight,
    scrim = scrimLight,
    inverseSurface = inverseSurfaceLight,
    inverseOnSurface = inverseOnSurfaceLight,
    inversePrimary = inversePrimaryLight,
    surfaceDim = surfaceDimLight,
    surfaceBright = surfaceBrightLight,
    surfaceContainerLowest = surfaceContainerLowestLight,
    surfaceContainerLow = surfaceContainerLowLight,
    surfaceContainer = surfaceContainerLight,
    surfaceContainerHigh = surfaceContainerHighLight,
    surfaceContainerHighest = surfaceContainerHighestLight,
)

private val gooseDarkColorScheme = darkColorScheme(
    primary = primaryDark,
    onPrimary = onPrimaryDark,
    primaryContainer = primaryContainerDark,
    onPrimaryContainer = onPrimaryContainerDark,
    secondary = secondaryDark,
    onSecondary = onSecondaryDark,
    secondaryContainer = secondaryContainerDark,
    onSecondaryContainer = onSecondaryContainerDark,
    tertiary = tertiaryDark,
    onTertiary = onTertiaryDark,
    tertiaryContainer = tertiaryContainerDark,
    onTertiaryContainer = onTertiaryContainerDark,
    error = errorDark,
    onError = onErrorDark,
    errorContainer = errorContainerDark,
    onErrorContainer = onErrorContainerDark,
    background = backgroundDark,
    onBackground = onBackgroundDark,
    surface = surfaceDark,
    onSurface = onSurfaceDark,
    surfaceVariant = surfaceVariantDark,
    onSurfaceVariant = onSurfaceVariantDark,
    outline = outlineDark,
    outlineVariant = outlineVariantDark,
    scrim = scrimDark,
    inverseSurface = inverseSurfaceDark,
    inverseOnSurface = inverseOnSurfaceDark,
    inversePrimary = inversePrimaryDark,
    surfaceDim = surfaceDimDark,
    surfaceBright = surfaceBrightDark,
    surfaceContainerLowest = surfaceContainerLowestDark,
    surfaceContainerLow = surfaceContainerLowDark,
    surfaceContainer = surfaceContainerDark,
    surfaceContainerHigh = surfaceContainerHighDark,
    surfaceContainerHighest = surfaceContainerHighestDark,
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

val LocalWindowSizeClass = compositionLocalOf<WindowSizeClass> {
    throw Exception("CompositionLocal WindowSize not present")
}

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
fun GooseTheme(
    themeConfig: ThemeConfig = remember { ThemeConfig(isDynamicColor = false) },
    useGooseStyle: Boolean = false,
    windowSizeClass: WindowSizeClass = (LocalContext.current as? Activity)?.let { act ->
        calculateWindowSizeClass(activity = act)
    } ?: LocalConfiguration.current.let { configuration ->
        WindowSizeClass.calculateFromSize(
            DpSize(configuration.screenWidthDp.dp, configuration.screenHeightDp.dp)
        )
    },
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    if (useGooseStyle) {
        LittleGooseStyle()
    }
    CompositionLocalProvider(LocalWindowSizeClass provides windowSizeClass) {
        MaterialTheme(
            colorScheme = themeConfig.getColorScheme(context = context),
            typography = Typography,
            content = content
        )
    }
}

private val Context.gooseStyleDataStore by preferencesDataStore("gooseStyle")

@Stable
object GooseStyle {
    @Stable
    var goose: Boolean by mutableStateOf(false)

    private val KEY = booleanPreferencesKey("key")

    suspend fun checkGoose(context: Context): Boolean {
        return context.gooseStyleDataStore.getDataOrNull(KEY)?.also { goose = it } ?: false
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
            if (GooseStyle.checkGoose(context)) return@LaunchedEffect
            findGooseStyleContainer(view).addView(
                TextView(context).apply {
                    gravity = Gravity.CENTER
                    setTextColor(colorScheme.primary.toArgb())
                    alpha = 0.56f
                    text = "\u8be5\u5f00\u6e90\u9879\u76ee\u4ec5\u4f9b\u5b66\u4e60"
                    textSize = 16F
                    tag = "goose"
                },
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            GooseStyle.goose = true
        }
    }
}

fun findGooseStyleContainer(view: View): ViewGroup {
    return if (view.parent is ComposeView) findGooseStyleContainer(view.parent as View)
    else view.parent as? ViewGroup ?: findGooseStyleContainer(view.parent as View)
}