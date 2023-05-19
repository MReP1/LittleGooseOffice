package little.goose.note.ui.note

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.android.awaitFrame
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import little.goose.design.system.theme.AccountTheme
import little.goose.note.data.constants.KEY_NOTE_ID
import little.goose.note.data.entities.NoteContentBlock

@AndroidEntryPoint
class NoteActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AccountTheme {
                NoteRoute(
                    modifier = Modifier.fillMaxSize(),
                    onBack = ::finish
                )
            }
        }
    }


    companion object {
        fun openAdd(context: Context) {
            val intent = Intent(context, NoteActivity::class.java)
            context.startActivity(intent)
        }

        fun openEdit(context: Context, noteId: Long) {
            val intent = Intent(context, NoteActivity::class.java).apply {
                putExtra(KEY_NOTE_ID, noteId)
            }
            context.startActivity(intent)
        }
    }

}

@Stable
sealed interface NoteRouteState {
    object Loading : NoteRouteState

    data class State(val state: NoteScreenState) : NoteRouteState
}

sealed class NoteScreenEvent {
    data class AddNoteBlock(val noteContentBlock: NoteContentBlock) : NoteScreenEvent()
}

@Composable
fun NoteRoute(
    modifier: Modifier = Modifier,
    onBack: () -> Unit
) {
    val viewModel = hiltViewModel<NoteViewModel>()

    val noteRouteState by viewModel.noteRouteState.collectAsStateWithLifecycle()

    when (val state = noteRouteState) {
        NoteRouteState.Loading -> {
            // TODO Loading Screen
        }

        is NoteRouteState.State -> {

            val blockColumnState = rememberLazyListState()

            NoteScreen(
                modifier = modifier,
                state = state.state,
                blockColumnState = blockColumnState,
                onBack = onBack
            )

            LaunchedEffect(viewModel.noteScreenEvent) {
                viewModel.noteScreenEvent.collectLatest { event ->
                    when (event) {
                        is NoteScreenEvent.AddNoteBlock -> {
                            val noteContentState = (noteRouteState as? NoteRouteState.State)
                                ?.state?.contentState ?: return@collectLatest
                            // 定位新增的 Block
                            val blockIndex = noteContentState.content
                                .indexOf(event.noteContentBlock)
                            if (blockIndex == noteContentState.content.lastIndex) {
                                // 最后一个 Block，滚动到底部，展示新增按钮
                                blockColumnState.animateScrollToItem(
                                    blockColumnState.layoutInfo.totalItemsCount - 1
                                )
                            } else if (blockIndex != -1) {
                                // 标题占了一个位置，所以要 +1
                                blockColumnState.animateScrollToItem(blockIndex + 1)
                            } else {
                                // 等待 focusRequester 被添加到 block component 中
                                delay(66)
                            }
                            // 为新增的 Block 申请焦点
                            var tryTime = 0
                            do {
                                tryTime++
                                val result = runCatching {
                                    noteContentState.focusRequesters[event.noteContentBlock.id]
                                        ?.requestFocus()
                                }.onFailure {
                                    awaitFrame()
                                }
                            } while (result.isFailure && tryTime < 5)
                        }
                    }
                }
            }
        }
    }
}