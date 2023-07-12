package little.goose.memorial.ui

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.rememberScrollableState
import androidx.compose.foundation.gestures.scrollable
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import little.goose.memorial.data.entities.Memorial
import little.goose.memorial.ui.component.MemorialColumn
import little.goose.memorial.ui.component.MemorialColumnState
import little.goose.memorial.ui.component.MemorialTitle
import little.goose.ui.surface.NestedPullSurface

@Composable
fun MemorialHome(
    modifier: Modifier,
    topMemorial: Memorial?,
    memorialColumnState: MemorialColumnState,
    onNavigateToMemorial: (Long) -> Unit,
    onNavigateToSearch: () -> Unit
) {
    NestedPullSurface(
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
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .scrollable(
                        rememberScrollableState(consumeScrollDelta = { 0F }),
                        Orientation.Vertical
                    ) // 由于父布局使用了嵌套滑动，此处图方便加了一个不消费的滑动
            ) {
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