package little.goose.memorial.ui.component

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import little.goose.memorial.data.entities.Memorial

data class MemorialColumnState(
    val memorials: List<Memorial>,
    val isMultiSelecting: Boolean,
    val multiSelectedMemorials: Set<Memorial>,
    val onSelectMemorial: (item: Memorial, selected: Boolean) -> Unit,
    val selectAllMemorial: () -> Unit,
    val cancelMultiSelecting: () -> Unit,
    val deleteMemorials: (memorials: List<Memorial>) -> Unit
)

@Composable
fun MemorialColumn(
    modifier: Modifier = Modifier,
    state: MemorialColumnState,
    onMemorialClick: (Memorial) -> Unit
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp)
    ) {
        items(
            items = state.memorials,
            key = { it.id ?: it }
        ) { memorial ->
            MemorialItem(
                modifier = Modifier.padding(vertical = 4.dp),
                memorial = memorial,
                isMultiSelecting = state.isMultiSelecting,
                selected = state.multiSelectedMemorials.contains(memorial),
                onMemorialClick = onMemorialClick,
                onSelectMemorial = state.onSelectMemorial
            )
        }
    }
}