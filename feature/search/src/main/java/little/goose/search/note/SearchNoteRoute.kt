package little.goose.search.note

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.flow.collectLatest
import little.goose.note.ui.search.SearchNoteEvent
import little.goose.note.ui.search.SearchNoteScreen
import little.goose.note.ui.search.rememberSearchNoteStateHolder

@Composable
internal fun SearchNoteRoute(
    modifier: Modifier = Modifier,
    onNavigateToNote: (Long) -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current

    val (state, event, action) = rememberSearchNoteStateHolder()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        event.collectLatest { event ->
            when (event) {
                SearchNoteEvent.DeleteNotes -> {
                    snackbarHostState.showSnackbar(
                        message = context.getString(little.goose.common.R.string.deleted)
                    )
                }
            }
        }
    }

    SearchNoteScreen(
        modifier = modifier,
        state = state,
        snackbarHostState = snackbarHostState,
        onNavigateToNote = onNavigateToNote,
        action = action,
        onBack = onBack
    )
}