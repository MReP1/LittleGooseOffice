package little.goose.note

import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import kotlinx.coroutines.flow.Flow
import little.goose.note.event.NoteScreenEvent
import little.goose.note.ui.note.NoteScreen
import little.goose.note.ui.note.NoteScreenIntent
import little.goose.note.ui.note.NoteScreenState
import org.koin.core.parameter.parametersOf

data class NoteScreen(val noteId: Long) : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = getScreenModel<NoteScreenModel> { parametersOf(noteId) }
        val screenState by screenModel.noteScreenStateHolder.noteScreenState.collectAsState()
        NoteScreenRoute(
            event = screenModel.noteScreenStateHolder.event,
            screenState = screenState,
            onBack = navigator::pop,
            action = screenModel.noteScreenStateHolder.action
        )
    }

}

@Composable
fun NoteScreenRoute(
    modifier: Modifier = Modifier,
    onBack: () -> Unit,
    event: Flow<NoteScreenEvent>,
    screenState: NoteScreenState,
    action: (NoteScreenIntent) -> Unit
) {
    val blockColumnState = rememberLazyListState()

    NoteScreen(
        modifier = modifier,
        onBack = onBack,
        noteScreenState = screenState,
        blockColumnState = blockColumnState,
        action = action
    )

    LaunchedEffect(event) {
        event.collect { event ->
            when (event) {
                is NoteScreenEvent.AddNoteBlock -> {
                    if (event.blockIndex != -1) {
                        // 标题占了一个位置，所以要 +1
                        blockColumnState.animateScrollToItem(event.blockIndex + 1, 0)
                    }
                    // 申请焦点
                    runCatching { event.focusRequester.requestFocus() }
                }
            }
        }
    }

}