package little.goose.note.ui.note

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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mikepenz.markdown.Markdown
import com.mikepenz.markdown.MarkdownDefaults
import little.goose.common.utils.generateUnitId
import little.goose.ui.screen.LittleGooseLoadingScreen

@Stable
sealed class NoteContentState {
    data object Loading : NoteContentState()

    data class Edit(
        val titleState: TextFieldState,
        val contentStateList: List<NoteBlockState>,
        val onBlockDelete: (Long) -> Unit = {},
        val onBlockAdd: () -> Unit = {}
    ) : NoteContentState()

    data class Preview(val content: String) : NoteContentState()
}


@Stable
class NoteBlockState(
    val id: Long,
    val contentState: TextFieldState,
    val interaction: MutableInteractionSource = MutableInteractionSource(),
    val focusRequester: FocusRequester = FocusRequester()
)

@Composable
fun NoteContent(
    modifier: Modifier = Modifier,
    state: NoteContentState,
    onAddBlock: () -> Unit,
    blockColumnState: LazyListState
) {
    Box(modifier = modifier) {
        when (state) {
            is NoteContentState.Edit -> {
                NoteEditContent(
                    modifier = Modifier.fillMaxSize(),
                    state = state,
                    onAddBlock = onAddBlock,
                    blockColumnState = blockColumnState
                )
            }

            NoteContentState.Loading -> {
                LittleGooseLoadingScreen(
                    modifier = Modifier.fillMaxSize()
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
    val colorScheme = MaterialTheme.colorScheme
    Markdown(
        content = state.content,
        modifier = modifier
            .verticalScroll(scrollState)
            .padding(16.dp),
        colors = MarkdownDefaults.markdownColors(
            textColor = MaterialTheme.colorScheme.onBackground,
            backgroundColor = MaterialTheme.colorScheme.onSurface,
            codeBackgroundColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f),
            colorByType = { colorScheme.onBackground }
        ),
        typography = MarkdownDefaults.markdownTypography(
            h1 = MaterialTheme.typography.headlineLarge,
            h2 = MaterialTheme.typography.headlineMedium,
            h3 = MaterialTheme.typography.headlineSmall,
            h4 = MaterialTheme.typography.titleLarge,
            h5 = MaterialTheme.typography.titleMedium.copy(fontSize = 18.sp),
            h6 = MaterialTheme.typography.titleSmall.copy(fontSize = 16.sp),
            body1 = MaterialTheme.typography.bodyMedium,
            body2 = MaterialTheme.typography.bodySmall,
        )
    )
}

@Composable
private fun NoteEditContent(
    modifier: Modifier = Modifier,
    state: NoteContentState.Edit,
    onAddBlock: () -> Unit,
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
                    onDone = {
                        onAddBlock()
                    }
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
                onBlockDelete = { state.onBlockDelete(contentState.id) },
                focusRequester = contentState.focusRequester,
                interactionSource = contentState.interaction
            )
        }
    }
}

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
            onAddBlock = {}
        )
    }
}