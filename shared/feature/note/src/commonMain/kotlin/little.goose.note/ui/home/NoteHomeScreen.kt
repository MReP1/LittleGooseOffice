package little.goose.note.ui.home

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import little.goose.note.NoteHomeState

@Composable
internal fun NoteHomeScreen(
    state: NoteHomeState,
    onNoteItemClick: (noteId: Long) -> Unit
) {

    Surface(modifier = Modifier.fillMaxSize()) {
        when (state) {
            NoteHomeState.Loading -> {

            }

            is NoteHomeState.Success -> {
                NoteColumn(
                    modifier = Modifier.fillMaxSize(),
                    itemStates = state.itemStates,
                    onNoteItemClick = onNoteItemClick
                )
            }
        }
    }


}