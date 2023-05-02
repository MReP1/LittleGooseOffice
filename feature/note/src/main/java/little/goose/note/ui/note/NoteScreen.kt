package little.goose.note.ui.note

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.isImeVisible
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.union
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.EditNote
import androidx.compose.material.icons.rounded.FormatListBulleted
import androidx.compose.material.icons.rounded.FormatListNumbered
import androidx.compose.material.icons.rounded.Preview
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.BottomAppBarDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mikepenz.markdown.Markdown
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import little.goose.design.system.theme.AccountTheme
import little.goose.note.R
import little.goose.note.data.entities.Note
import little.goose.note.logic.format
import little.goose.note.ui.component.FormatHeaderIcon


data class NoteScreenState(
    val note: Note = Note(),
    val onTitleChange: (String) -> Unit = {},
    val onContentChange: (String) -> Unit = {}
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun NoteScreen(
    modifier: Modifier = Modifier,
    state: NoteScreenState,
    onBack: () -> Unit
) {
    val scope = rememberCoroutineScope()
    var isPreview by remember { mutableStateOf(false) }

    var contentTextFieldValue by remember {
        mutableStateOf(TextFieldValue(text = state.note.content))
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                modifier = Modifier.fillMaxWidth(),
                title = {},
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Rounded.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
            )
        },
        bottomBar = {
            BottomAppBar(
                modifier = Modifier.fillMaxWidth(),
                windowInsets = if (WindowInsets.isImeVisible) {
                    WindowInsets.ime.union(BottomAppBarDefaults.windowInsets)
                } else {
                    BottomAppBarDefaults.windowInsets
                },
                content = {
                    Spacer(modifier = Modifier.width(12.dp))
                    val horScrollState = rememberScrollState()
                    Row(
                        modifier = Modifier
                            .weight(1F)
                            .horizontalScroll(horScrollState)
                    ) {
                        FormatHeaderIcon(
                            modifier = Modifier,
                            onHeaderClick = {
                                scope.launch(Dispatchers.Main.immediate) {
                                    contentTextFieldValue = contentTextFieldValue.format(it)
                                }
                            }
                        )
                        IconButton(
                            onClick = { /*TODO*/ }
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.FormatListBulleted,
                                contentDescription = "ListBullet"
                            )
                        }
                        IconButton(
                            onClick = { /*TODO*/ }
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.FormatListNumbered,
                                contentDescription = "ListBullet"
                            )
                        }
                    }
                    FloatingActionButton(
                        onClick = {
                            isPreview = !isPreview
                        }
                    ) {
                        AnimatedContent(targetState = isPreview) {
                            Icon(
                                imageVector = if (isPreview) {
                                    Icons.Rounded.Preview
                                } else {
                                    Icons.Rounded.EditNote
                                },
                                contentDescription = "Preview"
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                }
            )
        }
    ) { scaffoldPaddingValue ->
        Column(
            modifier = Modifier.padding(scaffoldPaddingValue)
        ) {
            TextField(
                value = state.note.title,
                onValueChange = state.onTitleChange,
                modifier = Modifier.fillMaxWidth(),
                maxLines = 1,
                label = {
                    Text(text = stringResource(id = R.string.title))
                },
                shape = RectangleShape,
                colors = TextFieldDefaults.textFieldColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )

            if (isPreview) {
                Markdown(
                    content = state.note.content,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                TextField(
                    value = contentTextFieldValue,
                    onValueChange = {
                        state.onContentChange(it.text)
                        contentTextFieldValue = it
                    },
                    modifier = Modifier.fillMaxSize(),
                    label = {
                        Text(text = stringResource(id = R.string.content))
                    },
                    colors = TextFieldDefaults.textFieldColors(
                        containerColor = MaterialTheme.colorScheme.background,
                        unfocusedIndicatorColor = MaterialTheme.colorScheme.background,
                        focusedIndicatorColor = MaterialTheme.colorScheme.background
                    )
                )
            }
        }
    }
}

@Preview
@Composable
private fun PreviewNoteScreen() = AccountTheme {
    NoteScreen(
        state = NoteScreenState(),
        onBack = {}
    )
}