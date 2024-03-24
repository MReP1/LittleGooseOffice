@file:OptIn(ExperimentalFoundationApi::class)

package little.goose.note.ui.note

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text2.BasicTextField2
import androidx.compose.foundation.text2.input.TextFieldLineLimits
import androidx.compose.foundation.text2.input.TextFieldState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mikepenz.markdown.compose.Markdown
import com.mikepenz.markdown.m3.markdownColor
import com.mikepenz.markdown.m3.markdownTypography

@Stable
sealed class NoteContentState {

    data class Edit(
        val titleState: TextFieldState,
        val contentStateList: List<NoteBlockState>
    ) : NoteContentState()

    data class Preview(val content: String) : NoteContentState()

}


@Stable
data class NoteBlockState(
    val id: Long,
    val contentState: TextFieldState,
    val interaction: MutableInteractionSource = MutableInteractionSource(),
    val focusRequester: FocusRequester = FocusRequester()
)

@Composable
fun NoteContent(
    modifier: Modifier = Modifier,
    state: NoteContentState,
    blockColumnState: LazyListState,
    action: (NoteScreenIntent) -> Unit
) {
    Box(modifier = modifier) {
        when (state) {
            is NoteContentState.Edit -> {
                NoteEditContent(
                    modifier = Modifier.fillMaxSize(),
                    state = state,
                    action = action,
                    blockColumnState = blockColumnState
                )
            }

            is NoteContentState.Preview -> {
                MarkdownContent(
                    modifier = Modifier.fillMaxSize(),
                    state = state
                )
            }
        }
    }
}

@Composable
fun MarkdownContent(
    modifier: Modifier = Modifier,
    state: NoteContentState.Preview
) {
    val scrollState = rememberScrollState()
    Markdown(
        content = state.content,
        modifier = modifier
            .verticalScroll(scrollState)
            .padding(16.dp),
        colors = markdownColor(
            text = MaterialTheme.colorScheme.onBackground,
            codeBackground = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f),
        ),
        typography = markdownTypography(
            h1 = MaterialTheme.typography.headlineLarge,
            h2 = MaterialTheme.typography.headlineMedium,
            h3 = MaterialTheme.typography.headlineSmall,
            h4 = MaterialTheme.typography.titleLarge,
            h5 = MaterialTheme.typography.titleMedium.copy(fontSize = 18.sp),
            h6 = MaterialTheme.typography.titleSmall.copy(fontSize = 16.sp),
            text = MaterialTheme.typography.bodyMedium,
        )
    )
}

@Composable
fun NoteEditContent(
    modifier: Modifier = Modifier,
    state: NoteContentState.Edit,
    action: (NoteScreenIntent) -> Unit,
    blockColumnState: LazyListState = rememberLazyListState()
) {
    LazyColumn(
        modifier = modifier,
        state = blockColumnState,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        item {
            BasicTextField2(
                state = state.titleState,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = 24.dp,
                        vertical = 12.dp
                    ),
                lineLimits = TextFieldLineLimits.SingleLine,
                textStyle = MaterialTheme.typography.titleLarge.copy(
                    color = LocalContentColor.current
                ),
                keyboardActions = KeyboardActions(
                    onDone = { action(NoteScreenIntent.AddBlockToBottom) }
                ),
                decorator = {
                    if (state.titleState.text.isEmpty()) {
                        Text(
                            text = "Title",
                            color = MaterialTheme.colorScheme.outline,
                            style = MaterialTheme.typography.titleLarge
                        )
                    }
                    it()
                }
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        items(
            count = state.contentStateList.size,
            key = { state.contentStateList[it].id }
        ) {
            val contentState = state.contentStateList[it]
            NoteContentBlockItem(
                modifier = Modifier
                    .fillMaxWidth()
                    .animateItemPlacement(),
                textFieldState = contentState.contentState,
                onBlockDelete = {
                    action(NoteScreenIntent.DeleteBlock(contentState.id))
                },
                focusRequester = contentState.focusRequester,
                interactionSource = contentState.interaction
            )
        }
    }
}