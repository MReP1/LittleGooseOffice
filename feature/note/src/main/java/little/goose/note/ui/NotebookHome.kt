package little.goose.note.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import little.goose.ui.surface.PullSurface

@Composable
fun NotebookHome(
    modifier: Modifier = Modifier,
    noteColumnState: NoteColumnState,
    onNavigateToNote: (noteId: Long) -> Unit,
    onNavigateToSearch: () -> Unit
) {
    PullSurface(
        modifier = modifier,
        onPull = onNavigateToSearch,
        backgroundContent = { progress ->
            Icon(
                imageVector = Icons.Rounded.Search,
                contentDescription = "Search",
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 12.dp)
                    .size(min(48.dp, 24.dp + 24.dp * progress))
                    .alpha(progress.coerceIn(0.62F, 1F))
            )
        },
        content = {
            NoteColumn(
                modifier = Modifier.fillMaxSize(),
                state = noteColumnState,
                onNoteClick = { note ->
                    note.id?.run(onNavigateToNote)
                }
            )
        }
    )
}