package little.goose.memorial.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import little.goose.memorial.data.entities.Memorial
import little.goose.memorial.ui.widget.MemorialColumn
import little.goose.memorial.ui.widget.MemorialTitle

@Composable
fun MemorialRoute(
    modifier: Modifier,
    onMemorialClick: (Memorial) -> Unit
) {
    val viewModel: MemorialFragmentViewModel = hiltViewModel()
    val memorials by viewModel.memorials.collectAsState()
    val topMemorial by viewModel.topMemorial.collectAsState()
    MemorialScreen(
        modifier = modifier,
        memorials = memorials,
        topMemorial = topMemorial,
        onMemorialClick = onMemorialClick
    )
}

@Composable
private fun MemorialScreen(
    modifier: Modifier,
    memorials: List<Memorial>,
    topMemorial: Memorial?,
    onMemorialClick: (Memorial) -> Unit
) {
    Surface(modifier = modifier) {
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
                memorials = memorials,
                onMemorialClick = onMemorialClick
            )
        }
    }
}