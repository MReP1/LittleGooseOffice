package little.goose.memorial.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import little.goose.common.utils.generateUnitId
import little.goose.common.utils.progressWith
import little.goose.design.system.theme.GooseTheme
import little.goose.memorial.data.entities.Memorial
import little.goose.memorial.ui.component.MemorialColumn
import little.goose.memorial.ui.component.MemorialTitle
import little.goose.ui.icon.PullToSearchIcon
import little.goose.ui.surface.PullSurface

@Composable
fun MemorialHome(
    modifier: Modifier,
    memorialHomeState: MemorialHomeState,
    onNavigateToMemorial: (Long) -> Unit,
    onNavigateToSearch: () -> Unit
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
            Column(modifier = Modifier.fillMaxSize()) {
                if (memorialHomeState.topMemorial != null) {
                    MemorialTitle(
                        modifier = Modifier
                            .height(130.dp)
                            .fillMaxWidth(),
                        memorial = memorialHomeState.topMemorial
                    )
                }
                MemorialColumn(
                    modifier = Modifier
                        .weight(1F)
                        .fillMaxWidth(),
                    state = memorialHomeState.memorialColumnState,
                    onMemorialEdit = { memorial ->
                        memorial.id?.run(onNavigateToMemorial)
                    }
                )
            }
        }
    )
}

@Preview
@Composable
fun PreviewMemorialHome() = GooseTheme {
    MemorialHome(
        modifier = Modifier.fillMaxSize(),
        memorialHomeState = MemorialHomeState(
            topMemorial = Memorial(content = "HelloWorld", isTop = true),
            memorialColumnState = MemorialColumnState(
                listOf(Memorial(id = generateUnitId()))
            )
        ),
        onNavigateToMemorial = {},
        onNavigateToSearch = {}
    )
}