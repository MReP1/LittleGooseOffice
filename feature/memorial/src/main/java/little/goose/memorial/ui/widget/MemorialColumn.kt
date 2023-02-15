package little.goose.memorial.ui.widget

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import little.goose.memorial.data.entities.Memorial

@Composable
fun MemorialColumn(
    modifier: Modifier = Modifier,
    memorials: List<Memorial>,
    onMemorialClick: (Memorial) -> Unit
) {
    LazyColumn(modifier = modifier) {
        items(
            items = memorials,
            key = { it.id ?: -1 }
        ) { memorial ->
            MemorialItem(
                modifier = Modifier.padding(16.dp),
                memorial = memorial,
                onMemorialClick = onMemorialClick
            )
        }
    }
}