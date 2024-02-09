package little.goose.note

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.navigator.Navigator
import little.goose.design.theme.GooseTheme
import little.goose.design.theme.ThemeType

@Composable
fun NoteApp() {
    GooseTheme(ThemeType.FOLLOW_SYSTEM) {
        Navigator(NoteHomeScreen)
    }
}