package little.goose.design.system.theme

import android.content.Context
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
)

private val LightColorScheme = lightColorScheme(
    primary = PrimaryColor,
    onPrimary = OnPrimaryColorLight,
    secondary = SecondColor,
    onSecondary = OnSecondaryColorLight,
    background = BackgroundColor,
    surface = SurfaceColor,
    onSurface = OnSurfaceColor,
    surfaceVariant = SurfaceVariantColor,
    onSurfaceVariant = OnSurfaceVariantColor
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
    MaterialTheme(
        colorScheme = themeConfig.getColorScheme(context = context),
        typography = Typography,
        content = content
    )
}