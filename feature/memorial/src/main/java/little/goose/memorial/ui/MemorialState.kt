package little.goose.memorial.ui

import androidx.compose.runtime.Stable
import little.goose.memorial.data.entities.Memorial

@Stable
data class MemorialHomeState(
    val topMemorial: Memorial? = null,
    val memorialColumnState: MemorialColumnState = MemorialColumnState()
)

@Stable
data class MemorialColumnState(
    val memorials: List<Memorial> = listOf(),
    val isMultiSelecting: Boolean = false,
    val multiSelectedMemorials: Set<Memorial> = emptySet(),
    val onSelectMemorial: (item: Memorial, selected: Boolean) -> Unit = { _, _ -> },
    val selectAllMemorial: () -> Unit = {},
    val cancelMultiSelecting: () -> Unit = {},
    val deleteMemorials: (memorials: List<Memorial>) -> Unit = {}
)