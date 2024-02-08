package little.goose.note

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.navigator.Navigator

@Composable
fun NoteApp() {
    Navigator(NoteScreen(-1))
}