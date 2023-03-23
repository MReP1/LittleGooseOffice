package little.goose.search

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import little.goose.memorial.data.entities.Memorial
import little.goose.memorial.ui.MemorialDialog
import little.goose.memorial.ui.component.MemorialColumn
import little.goose.memorial.ui.rememberMemorialDialogState

@Composable
fun SearchMemorialScreen(
    modifier: Modifier = Modifier,
    memorials: List<Memorial>,
    onDeleteMemorial: (Memorial) -> Unit
) {
    if (memorials.isNotEmpty()) {
        val memorialDialogState = rememberMemorialDialogState()
        MemorialDialog(
            state = memorialDialogState,
            onDelete = onDeleteMemorial
        )
        MemorialColumn(
            modifier = modifier.fillMaxSize(),
            memorials = memorials,
            onMemorialClick = {
                memorialDialogState.show(it)
            }
        )
    }
}