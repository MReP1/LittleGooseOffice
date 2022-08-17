package little.goose.account.ui.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = Red200,
    secondary = Teal200
)

private val LightColorScheme = lightColorScheme(
    primary = Red500,
    onPrimary = OnPrimaryColorLight,
    secondary = Red200,
    onSecondary = OnSecondaryColorLight,
    background = BackgroundColor,
    surface = SurfaceColor,             //表面颜色
    onSurface = OnSurfaceColor,
    surfaceVariant = SurfaceVariantColor,      //卡片颜色
    onSurfaceVariant = OnSurfaceVariantColor
)

@Composable
fun AccountTheme(content: @Composable () -> Unit) {
    //todo 适配夜间模式
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = Typography,
        content = content
    )
}