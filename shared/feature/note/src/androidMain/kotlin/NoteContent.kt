import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.text2.input.TextFieldState
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import little.goose.note.ui.note.NoteBlockState
import little.goose.note.ui.note.NoteContentState
import little.goose.note.ui.note.NoteEditContent
import little.goose.shared.common.generateUnitId

@OptIn(ExperimentalFoundationApi::class)
@Preview
@Composable
fun PreviewNoteEditContent() {
    Surface(
        modifier = Modifier.fillMaxSize()
    ) {
        val state = remember {
            NoteContentState.Edit(
                titleState = TextFieldState(),
                contentStateList = List(10) {
                    NoteBlockState(
                        id = generateUnitId(),
                        contentState = TextFieldState(
                            initialText = System.currentTimeMillis().toString()
                        )
                    )
                }
            )
        }
        NoteEditContent(
            state = state,
            modifier = Modifier.fillMaxSize(),
            action = {}
        )
    }
}