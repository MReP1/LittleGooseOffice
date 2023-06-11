package little.goose.memorial.ui.component

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import little.goose.design.system.theme.AccountTheme
import little.goose.memorial.data.entities.Memorial

data class MemorialColumnState(
    val memorials: List<Memorial> = listOf(),
    val isMultiSelecting: Boolean = false,
    val multiSelectedMemorials: Set<Memorial> = emptySet(),
    val onSelectMemorial: (item: Memorial, selected: Boolean) -> Unit = { _, _ -> },
    val selectAllMemorial: () -> Unit = {},
    val cancelMultiSelecting: () -> Unit = {},
    val deleteMemorials: (memorials: List<Memorial>) -> Unit = {}
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

@Preview
@Composable
private fun PreviewMemorialColumn() = AccountTheme {
    MemorialColumn(
        state = MemorialColumnState(
            memorials = (0..5).map {
                Memorial(id = it.toLong(), content = "Memorial$it")
            },
            isMultiSelecting = false,
            multiSelectedMemorials = emptySet(),
            onSelectMemorial = { _, _ -> },
            selectAllMemorial = {},
            cancelMultiSelecting = {},
            deleteMemorials = {}
        ),
        onMemorialClick = {}
    )
}