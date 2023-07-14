package little.goose.memorial.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import little.goose.memorial.data.entities.Memorial
import little.goose.memorial.ui.component.MemorialColumn
import little.goose.memorial.ui.component.MemorialColumnState
import little.goose.memorial.ui.component.MemorialTitle
import little.goose.ui.surface.PullSurface

@Composable
fun MemorialHome(
    modifier: Modifier,
    topMemorial: Memorial?,
    memorialColumnState: MemorialColumnState,
    onNavigateToMemorial: (Long) -> Unit,
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
                    .padding(top = 12.dp)
                    .size(min(48.dp, 24.dp + 24.dp * progress))
                    .alpha(progress.coerceIn(0.62F, 1F))
            )
        },
        content = {
            Column(modifier = Modifier.fillMaxSize()) {
                if (topMemorial != null) {
                    MemorialTitle(
                        modifier = Modifier
                            .height(130.dp)
                            .fillMaxWidth(),
                        memorial = topMemorial
                    )
                }
                MemorialColumn(
                    modifier = Modifier
                        .weight(1F)
                        .fillMaxWidth(),
                    state = memorialColumnState,
                    onMemorialEdit = { memorial ->
                        memorial.id?.run(onNavigateToMemorial)
                    }
                )
            }
        }
    )
}