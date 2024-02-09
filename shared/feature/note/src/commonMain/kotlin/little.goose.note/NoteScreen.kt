package little.goose.note

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import kotlinx.coroutines.delay
import little.goose.data.note.local.NoteDataBase
import little.goose.note.event.NoteScreenEvent
import little.goose.note.ui.note.NoteContentState
import little.goose.note.ui.note.NoteScreen
import org.koin.compose.getKoin

data class NoteScreen(val noteId: Long) : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val noteDatabase = getKoin().get<NoteDataBase>()
        val screenModel = rememberScreenModel(noteId.toString()) {
            NoteScreenModel(noteId, noteDatabase)
        }
        val contentState by screenModel.noteContentState.collectAsState()
        val bottomBarState by screenModel.noteBottomBarState.collectAsState()

        val blockColumnState = rememberLazyListState()

        NoteScreen(
            onBack = navigator::pop,
            modifier = Modifier.fillMaxSize(),
            bottomBarState = bottomBarState,
            noteContentState = contentState,
            blockColumnState = blockColumnState
        )

        LaunchedEffect(Unit) {
            screenModel.event.collect { event ->
                when (event) {
                    is NoteScreenEvent.AddNoteBlock -> {
                        val editState = contentState as? NoteContentState.Edit ?: return@collect
                        val blockIndex = editState.contentStateList.indexOfLast {
                            it.id == event.id
                        }
                        delay(20)
                        if (blockIndex != -1) {
                            // 标题占了一个位置，所以要 +1
                            blockColumnState.animateScrollToItem(blockIndex + 1, 0)
                        }
                        // 申请焦点
                        runCatching {
                            editState.contentStateList
                                .getOrNull(blockIndex)
                                ?.focusRequester
                                ?.requestFocus()
                        }
                    }
                }
            }
        }
    }

}