package little.goose.note

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import little.goose.note.ui.notebook.NoteColumnState
import little.goose.note.ui.notebook.NotebookHome
import little.goose.note.ui.notebook.NotebookIntent
import little.goose.note.ui.notebook.rememberNotebookHomeStateHolder
import little.goose.note.ui.search.SearchNoteScreen
import little.goose.resource.GooseRes
import org.jetbrains.compose.resources.getString

object NotebookHomeScreen : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        NotebookHomeRoute(
            modifier = Modifier.fillMaxSize(),
            onNavigateToNote = { navigator.push(NoteScreen(it)) },
            onNavigateToSearch = { navigator.push(SearchNoteScreen) }
        )
    }

}

@Composable
fun NotebookHomeRoute(
    modifier: Modifier,
    onNavigateToNote: (Long) -> Unit,
    onNavigateToSearch: () -> Unit
) {
    val (state, event, action) = rememberNotebookHomeStateHolder()
    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(event) {
        event.collect {
            snackbarHostState.showSnackbar(getString(GooseRes.strings.deleted))
        }
    }
    NotebookHomeScreen(
        modifier = modifier,
        snackbarHostState = snackbarHostState,
        state = state,
        action = action,
        onNavigateToNote = onNavigateToNote,
        onNavigateToSearch = onNavigateToSearch,
    )
}

@Composable
private fun NotebookHomeScreen(
    modifier: Modifier,
    snackbarHostState: SnackbarHostState,
    state: NoteColumnState,
    action: (NotebookIntent) -> Unit,
    onNavigateToNote: (Long) -> Unit,
    onNavigateToSearch: () -> Unit
) {
    Scaffold(
        modifier = modifier,
        content = {
            NotebookHome(
                modifier = Modifier.fillMaxSize().padding(it),
                noteColumnState = state,
                onNavigateToNote = onNavigateToNote,
                onNavigateToSearch = onNavigateToSearch,
                action = action
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onNavigateToNote(-1) }
            ) {
                Icon(Icons.Rounded.Add, contentDescription = "Add Note")
            }
        },
        floatingActionButtonPosition = FabPosition.Center,
        topBar = {
            TopAppBar(title = {
                Text(text = "Little Goose Note")
            })
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        }
    )
}