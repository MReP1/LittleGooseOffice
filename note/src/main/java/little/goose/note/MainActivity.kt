package little.goose.note

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.DisposableEffect
import little.goose.design.theme.GooseTheme
import little.goose.design.theme.ThemeType
import little.goose.design.theme.isDarkTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val themeType = ThemeType.FOLLOW_SYSTEM
            val darkTheme = themeType.isDarkTheme()
            DisposableEffect(darkTheme) {
                enableEdgeToEdge(
                    SystemBarStyle.auto(Color.TRANSPARENT, Color.TRANSPARENT) { darkTheme },
                    SystemBarStyle.auto(lightScrim, darkScrim) { darkTheme }
                )
                onDispose { }
            }
            GooseTheme(themeType = themeType) {
                NoteApp()
            }
        }
    }
}

private val lightScrim = Color.argb(0xe6, 0xFF, 0xFF, 0xFF)
private val darkScrim = Color.argb(0x80, 0x1b, 0x1b, 0x1b)