package little.goose.note.ui.note

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mikepenz.markdown.Markdown
import com.mikepenz.markdown.MarkdownDefaults
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import little.goose.note.R
import little.goose.note.data.entities.Note
import little.goose.note.data.entities.NoteContentBlock

data class NoteContentState(
    val note: Note = Note(id = null),
    val focusingBlockId: Long? = null,
    val isPreview: Boolean = false,
    val content: List<NoteContentBlock> = listOf(),
    val interactions: Map<Long, MutableInteractionSource> = mapOf(),
    val focusRequesters: Map<Long, FocusRequester> = mapOf(),
    val textFieldValues: Map<Long, TextFieldValue> = mapOf(),
    val onBlockChange: (Int, Long, TextFieldValue) -> Unit = { _, _, _ -> },
    val onBlockAdd: (NoteContentBlock) -> Unit = {},
    val onTitleChange: (String) -> Unit = {},
    val onBlockDelete: (NoteContentBlock) -> Unit = {}
)

@Composable
fun NoteContent(
    modifier: Modifier = Modifier,
    state: NoteContentState,
    blockColumnState: LazyListState
) {
    Box(
        modifier = modifier.fillMaxSize()
    ) {
        if (state.isPreview) {
            MarkdownContent(
                modifier = Modifier.fillMaxSize(),
                state = state
            )
        } else {
            NoteEditContent(
                modifier = Modifier.fillMaxWidth(),
                state = state,
                blockColumnState = blockColumnState
            )
        }
    }
}

@Composable
private fun MarkdownContent(
    modifier: Modifier = Modifier,
    state: NoteContentState
) {
    val fullContent by produceState(initialValue = "", key1 = state.content) {
        value = withContext(Dispatchers.Default) {
            buildString {
                if (state.note.title.isNotBlank()) {
                    append("# ${state.note.title}\n\n")
                }
                append(state.content.joinToString("\n\n") { it.content })
            }
        }
    }
    val scrollState = rememberScrollState()
    Markdown(
        content = fullContent,
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        colors = MarkdownDefaults.markdownColors(
            textColor = MaterialTheme.colorScheme.onBackground,
            backgroundColor = MaterialTheme.colorScheme.onSurface,
            codeBackgroundColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f),
            colorByType = null
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
fun NoteEditContent(
    modifier: Modifier = Modifier,
    state: NoteContentState,
    blockColumnState: LazyListState
) {
    LazyColumn(
        modifier = modifier,
        state = blockColumnState
    ) {
        item {
            TextField(
                value = state.note.title,
                onValueChange = state.onTitleChange,
                modifier = Modifier.fillMaxWidth(),
                maxLines = 1,
                label = {
                    Text(text = stringResource(id = R.string.title))
                },
                shape = RectangleShape,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.background,
                    unfocusedContainerColor = MaterialTheme.colorScheme.background,
                    disabledContainerColor = MaterialTheme.colorScheme.background,
                )
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        items(
            count = state.content.size,
            key = { state.content[it].id ?: 0 }
        ) { index ->
            val block = state.content[index]
            NoteContentBlockItem(
                modifier = Modifier
                    .fillMaxWidth()
                    .animateItemPlacement(),
                value = state.textFieldValues[block.id]!!,
                focusRequester = state.focusRequesters[block.id]!!,
                interactionSource = state.interactions[block.id]!!,
                onValueChange = { value ->
                    block.id?.let { blockId ->
                        state.onBlockChange(block.index, blockId, value)
                    }
                },
                onBlockDelete = {
                    state.onBlockDelete(block)
                }
            )
        }

        item {
            Card(
                onClick = {
                    state.onBlockAdd(
                        NoteContentBlock(
                            id = null,
                            noteId = state.note.id,
                            index = state.content.size,
                            content = ""
                        )
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp)
            ) {
                Icon(
                    Icons.Rounded.Add,
                    contentDescription = "Add a block",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(34.dp)
                )
            }
        }
    }
}