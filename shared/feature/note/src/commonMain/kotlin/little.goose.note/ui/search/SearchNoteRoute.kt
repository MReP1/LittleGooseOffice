package little.goose.note.ui.search

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun SearchNoteRoute(
    modifier: Modifier = Modifier,
    onNavigateToNote: (Long) -> Unit,
    onBack: () -> Unit
) {
    val (state, event, action) = rememberSearchNoteStateHolder()
    SearchNoteScreen(
        modifier = modifier,
        state = state,
        event = event,
        onNavigateToNote = onNavigateToNote,
        action = action,
        onBack = onBack
    )
}