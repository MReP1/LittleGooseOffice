package little.goose.search.note

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import little.goose.design.system.theme.AccountTheme
import little.goose.note.data.entities.Note
import little.goose.note.data.entities.NoteContentBlock
import little.goose.note.ui.NoteColumnState
import little.goose.search.R
import little.goose.search.SearchNoteViewModel
import little.goose.search.component.SearchTopAppBar
import little.goose.ui.screen.LittleGooseLoadingScreen

sealed interface SearchNoteState {
    object Loading : SearchNoteState
    data class Success(val data: NoteColumnState) : SearchNoteState
    object Empty : SearchNoteState
}

sealed interface SearchNoteEvent {
    data class DeleteNotes(val notes: List<Note>) : SearchNoteEvent
}

@Composable
fun SearchNoteRoute(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val viewModel = hiltViewModel<SearchNoteViewModel>()
    val searchNoteState by viewModel.searchNoteState.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.searchNoteEvent.collect { event ->
            when (event) {
                is SearchNoteEvent.DeleteNotes -> {
                    snackbarHostState.showSnackbar(message = context.getString(R.string.deleted))
                }
            }
        }
    }

    SearchNoteScreen(
        modifier = modifier,
        state = searchNoteState,
        snackbarHostState = snackbarHostState,
        onSearch = viewModel::search
    )
}

@Composable
fun SearchNoteScreen(
    modifier: Modifier = Modifier,
    state: SearchNoteState,
    snackbarHostState: SnackbarHostState,
    onSearch: (String) -> Unit = {}
) {
    Scaffold(
        modifier = modifier,
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) { snackbarData ->
                Snackbar(snackbarData)
            }
        },
        topBar = {
            var keyword by remember { mutableStateOf("") }
            SearchTopAppBar(
                keyword = keyword,
                onKeywordChange = {
                    keyword = it
                    onSearch(it)
                }
            )
        },
        content = {
            val contentModifier = Modifier
                .fillMaxSize()
                .padding(it)

            when (state) {
                SearchNoteState.Empty -> {
                    LittleGooseLoadingScreen(modifier = contentModifier)
                }

                SearchNoteState.Loading -> {
                    LittleGooseLoadingScreen(modifier = contentModifier)
                }

                is SearchNoteState.Success -> {
                    SearchNoteContent(
                        modifier = contentModifier,
                        noteColumnState = state.data
                    )
                }
            }
        }
    )
}

@Preview
@Composable
private fun PreviewSearchNoteScreen() = AccountTheme {
    SearchNoteScreen(
        state = SearchNoteState.Success(
            data = NoteColumnState(
                noteWithContents = mapOf(
                    Note() to listOf(NoteContentBlock(content = "Preview"))
                ),
                isMultiSelecting = false,
                multiSelectedNotes = emptySet()
            ),
        ),
        snackbarHostState = SnackbarHostState()
    )
}