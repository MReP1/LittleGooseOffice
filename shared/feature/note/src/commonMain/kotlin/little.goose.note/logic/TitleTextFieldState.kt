@file:Suppress("FunctionName")

package little.goose.note.logic

import androidx.compose.foundation.text2.input.TextFieldState
import androidx.compose.foundation.text2.input.textAsFlow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import little.goose.data.note.bean.Note
import little.goose.data.note.bean.NoteWithContent

fun TitleTextFieldState(
    coroutineScope: CoroutineScope,
    getNoteWithContent: () -> NoteWithContent?,
    getNoteId: () -> Long,
    updateNoteId: (Long) -> Unit,
    insertOrReplaceNote: suspend (Note) -> Long
) = TextFieldState().apply {
    textAsFlow().onEach { title ->
        getNoteWithContent()?.note?.copy(title = title.toString())?.let { note ->
            val id = insertOrReplaceNote(note)
            if (getNoteId() == -1L) {
                updateNoteId(id)
            }
        }
    }.launchIn(coroutineScope)
}