package little.goose.design.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable

enum class ThemeType {
    LIGHT, DART, FOLLOW_SYSTEM
}

@Composable
fun ThemeType.isDarkTheme(): Boolean {
    return when (this) {
        ThemeType.LIGHT -> false
        ThemeType.DART -> true
        ThemeType.FOLLOW_SYSTEM -> isSystemInDarkTheme()
    }
}

@Composable
fun GooseTheme(
    themeType: ThemeType,
    content: @Composable () -> Unit
) {
    val colorScheme = when (themeType) {
        ThemeType.LIGHT -> gooseLightColorScheme
        ThemeType.DART -> gooseDarkColorScheme
        ThemeType.FOLLOW_SYSTEM -> if (isSystemInDarkTheme()) {
            gooseDarkColorScheme
        } else {
            gooseLightColorScheme
        }
    }
    MaterialTheme(colorScheme = colorScheme, content = content)
}