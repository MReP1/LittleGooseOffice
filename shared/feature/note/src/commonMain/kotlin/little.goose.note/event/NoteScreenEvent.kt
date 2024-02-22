package little.goose.note.event

import androidx.compose.ui.focus.FocusRequester

sealed class NoteScreenEvent {

    data class AddNoteBlock(
        val blockIndex: Int,
        val focusRequester: FocusRequester
    ) : NoteScreenEvent()

}