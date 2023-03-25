package little.goose.design.system.component

import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.interaction.DragInteraction
import androidx.compose.foundation.interaction.Interaction
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

@Composable
fun ScrollSelector(
    modifier: Modifier = Modifier,
    items: List<String>,
    state: LazyListState = rememberLazyListState(),
    textStyle: TextStyle = MaterialTheme.typography.bodyMedium,
    onItemSelected: (index: Int, content: String) -> Unit,
    unselectedScale: Float = 1.0F,
    selectedScale: Float = 1.6F
) {
    val density = LocalDensity.current
    val scope = rememberCoroutineScope()
    val currentUnselectedScale by rememberUpdatedState(newValue = unselectedScale)
    val currentSelectedScale by rememberUpdatedState(newValue = selectedScale)
    var contentSize by remember { mutableStateOf(IntSize.Zero) }
    val secondScale = remember { Animatable(selectedScale) }
    val thirdScale = remember { Animatable(unselectedScale) }
    var firstVisibleItemIndex by remember { mutableStateOf(0) }

    LaunchedEffect(state) {
        var lastInteraction: Interaction? = null
        var lastFirstVisibleItemScrollOffset = 0
        var needReset = false
        launch {
            combine(
                snapshotFlow { state.firstVisibleItemIndex },
                snapshotFlow { state.firstVisibleItemScrollOffset }
            ) { lastFirstVisibleItemIndex, firstVisibleItemScrollOffset ->
                val progress = firstVisibleItemScrollOffset.toFloat() / contentSize.height.toFloat()
                val disparity = (currentSelectedScale - currentUnselectedScale) * progress
                secondScale.snapTo(currentSelectedScale - disparity)
                thirdScale.snapTo(currentUnselectedScale + disparity)
                lastFirstVisibleItemScrollOffset = firstVisibleItemScrollOffset
                firstVisibleItemIndex = lastFirstVisibleItemIndex
            }.collect()
        }
        launch {
            state.interactionSource.interactions.collect { interaction ->
                if (interaction is DragInteraction) {
                    needReset = lastInteraction is DragInteraction.Start
                            && ((interaction as? DragInteraction.Stop)?.start == lastInteraction
                            || (interaction as? DragInteraction.Cancel)?.start == lastInteraction)
                    lastInteraction = interaction
                }
            }
        }
        launch {
            snapshotFlow { state.isScrollInProgress }.collect { isScroll ->
                if (!isScroll && needReset) {
                    needReset = false
                    scope.launch(Dispatchers.Main.immediate) {
                        val halfHeight = contentSize.height / 2
                        if (lastFirstVisibleItemScrollOffset < halfHeight) {
                            if (firstVisibleItemIndex < items.size) {
                                onItemSelected(
                                    firstVisibleItemIndex,
                                    items[firstVisibleItemIndex]
                                )
                            }
                            state.animateScrollToItem(firstVisibleItemIndex)
                        } else {
                            val selectedIndex = firstVisibleItemIndex + 1
                            if (selectedIndex < items.size) {
                                onItemSelected(
                                    firstVisibleItemIndex + 1,
                                    items[firstVisibleItemIndex + 1]
                                )
                            }
                            state.animateScrollToItem(firstVisibleItemIndex + 1)
                        }
                    }
                }
            }
        }
    }
    Box(
        modifier = modifier.fillMaxHeight(),
        contentAlignment = Alignment.Center
    ) {
        LazyColumn(
            modifier = Modifier.height(with(density) { (contentSize.height * 3).toDp() }),
            state = state
        ) {
            item {
                Column(
                    modifier = Modifier
                        .defaultMinSize(minWidth = 44.dp)
                        .onSizeChanged { size ->
                            if (contentSize == IntSize.Zero) {
                                contentSize = size
                            }
                        },
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(14.dp))
                    Text(text = "", style = textStyle, modifier = Modifier)
                    Spacer(modifier = Modifier.height(14.dp))
                }
            }
            items(
                count = items.size,
                key = { items[it] }
            ) { index ->
                Column(
                    modifier = Modifier
                        .defaultMinSize(minWidth = 44.dp)
                        .onSizeChanged { size ->
                            if (contentSize == IntSize.Zero) {
                                contentSize = size
                            }
                        },
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = items[index],
                        style = textStyle,
                        modifier = Modifier.scale(
                            when (index) {
                                firstVisibleItemIndex -> secondScale.value
                                firstVisibleItemIndex + 1 -> thirdScale.value
                                else -> currentUnselectedScale
                            }
                        )
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                }
            }
            item {
                Column(
                    modifier = Modifier
                        .defaultMinSize(minWidth = 44.dp)
                        .onSizeChanged { size ->
                            if (contentSize == IntSize.Zero) {
                                contentSize = size
                            }
                        },
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(14.dp))
                    Text(text = "", style = textStyle, modifier = Modifier)
                    Spacer(modifier = Modifier.height(14.dp))
                }
            }
        }
    }
}