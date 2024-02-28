package little.goose.note.ui.notebook

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.unit.dp
import little.goose.shared.common.utils.progressWith
import little.goose.shared.ui.icon.PullToSearchIcon
import little.goose.shared.ui.surface.PullSurface

@Composable
fun NotebookHome(
    modifier: Modifier = Modifier,
    noteColumnState: NoteColumnState,
    onNavigateToNote: (noteId: Long) -> Unit,
    onNavigateToSearch: () -> Unit,
    action: (NotebookIntent) -> Unit
) {
    PullSurface(
        modifier = modifier,
        onPull = onNavigateToSearch,
        backgroundContent = { progress ->
            PullToSearchIcon(
                modifier = Modifier
                    .padding(top = 6.dp)
                    .size(48.dp)
                    .offset(y = 32.dp * (progress - 1F))
                    .scale(progress.coerceIn(0.75F, 1F))
                    .alpha(progress.coerceIn(0.75F, 1F)),
                progress = progress.progressWith(0.52F, 0F, 1F),
                contentDescription = "Search",
            )
        },
        content = {
            NoteColumn(
                modifier = Modifier.fillMaxSize(),
                state = noteColumnState,
                onNoteClick = onNavigateToNote,
                onSelectNote = { noteId, selected ->
                    action(NotebookIntent.SelectNote(noteId, selected))
                }
            )
        }
    )
}