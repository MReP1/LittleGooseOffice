package little.goose.design.system.component

import androidx.compose.foundation.interaction.DragInteraction
import androidx.compose.foundation.interaction.Interaction
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@OptIn(ExperimentalTextApi::class)
@Composable
fun ScrollSelector(
    modifier: Modifier = Modifier,
    items: List<String>,
    state: LazyListState = rememberLazyListState(),
    textStyle: TextStyle = MaterialTheme.typography.titleLarge,
    onItemSelected: (index: Int, content: String) -> Unit,
    unselectedScale: Float = 0.68F,
    selectedScale: Float = 1.0F,
    padding: PaddingValues = PaddingValues(top = 14.dp, bottom = 14.dp)
) {
    val density = LocalDensity.current
    val currentUnselectedScale by rememberUpdatedState(newValue = unselectedScale)
    val currentSelectedScale by rememberUpdatedState(newValue = selectedScale)

    val textMeasurer = rememberTextMeasurer()
    val contentHeight = remember(textStyle, padding) {
        val textContentHeight = textMeasurer.measure("", textStyle).size.height
        val topPadding = padding.calculateTopPadding()
        val bottomPadding = padding.calculateBottomPadding()
        with(density) { (topPadding + bottomPadding).toPx() } + textContentHeight
    }

    val scrollingOutScale = remember { mutableStateOf(selectedScale) }
    val scrollingInScale = remember { mutableStateOf(unselectedScale) }
    var firstVisibleItemIndex by remember { mutableStateOf(state.firstVisibleItemIndex) }

    LaunchedEffect(state) {

        snapshotFlow { state.firstVisibleItemScrollOffset }
            .onEach { firstVisibleItemScrollOffset ->
                // 滑动时，根据滑动距离计算缩放比例
                val progress = firstVisibleItemScrollOffset.toFloat() / contentHeight
                val disparity = (currentSelectedScale - currentUnselectedScale) * progress
                scrollingOutScale.value = currentSelectedScale - disparity
                scrollingInScale.value = currentUnselectedScale + disparity
            }.launchIn(this)

        snapshotFlow { state.firstVisibleItemIndex }
            .filter { it != firstVisibleItemIndex }
            .onEach { firstVisibleItemIndex = it }
            .launchIn(this)

        launch {
            var lastInteraction: Interaction? = null
            state.interactionSource.interactions.mapNotNull {
                it as? DragInteraction
            }.map { interaction ->
                // 滑动结束或取消时，判断是否需要复位
                val currentStart = (interaction as? DragInteraction.Stop)?.start
                    ?: (interaction as? DragInteraction.Cancel)?.start
                val needReset = currentStart == lastInteraction
                lastInteraction = interaction
                needReset
            }.combine(snapshotFlow { state.isScrollInProgress }) { needReset, isScrollInProgress ->
                needReset && !isScrollInProgress
            }.filter {
                it
            }.collectLatest {
                val halfHeight = contentHeight / 2
                val selectedIndex = if (state.firstVisibleItemScrollOffset < halfHeight) {
                    // 若滑动距离小于一半，则回滚到上一个item
                    firstVisibleItemIndex
                } else {
                    // 若滑动距离大于一半，则滚动到下一个item
                    firstVisibleItemIndex + 1
                }
                if (selectedIndex < items.size) {
                    onItemSelected(selectedIndex, items[selectedIndex])
                }
                state.animateScrollToItem(selectedIndex)
            }
        }
    }

    Box(
        modifier = modifier
            .defaultMinSize(minWidth = 42.dp)
            .fillMaxHeight(),
        contentAlignment = Alignment.Center
    ) {
        LazyColumn(
            modifier = Modifier.height(
                with(density) { (contentHeight * 3).toDp() }
            ),
            state = state
        ) {
            item {
                Spacer(
                    modifier = Modifier.size(
                        width = 42.dp,
                        height = with(density) {
                            contentHeight.toDp()
                        }
                    )
                )
            }
            items(
                count = items.size,
                key = { items[it] }
            ) { index ->
                Box(
                    modifier = Modifier.defaultMinSize(minWidth = 42.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = items[index],
                        style = textStyle,
                        modifier = Modifier
                            .scale(
                                when (index) {
                                    firstVisibleItemIndex -> scrollingOutScale.value
                                    firstVisibleItemIndex + 1 -> scrollingInScale.value
                                    else -> currentUnselectedScale
                                }
                            )
                            .padding(padding)
                    )
                }
            }
            item {
                Spacer(
                    modifier = Modifier.size(
                        width = 42.dp,
                        height = with(density) {
                            contentHeight.toDp()
                        }
                    )
                )
            }
        }
    }
}