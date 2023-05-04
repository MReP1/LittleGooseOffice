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
    val contentHeight = remember(textStyle) {
        with(density) {
            val textContentHeight = textMeasurer.measure("", textStyle).size.height
            val topPadding = padding.calculateTopPadding()
            val bottomPadding = padding.calculateBottomPadding()
            (topPadding + bottomPadding).toPx() + textContentHeight
        }
    }

    val scrollingOutScale = remember { mutableStateOf(selectedScale) }
    val scrollingInScale = remember { mutableStateOf(unselectedScale) }
    var firstVisibleItemIndex by remember { mutableStateOf(0) }

    LaunchedEffect(state) {

        var lastFirstVisibleItemScrollOffset = 0
        combine(
            snapshotFlow { state.firstVisibleItemIndex },
            snapshotFlow { state.firstVisibleItemScrollOffset }
        ) { lastFirstVisibleItemIndex, firstVisibleItemScrollOffset ->
            // 滑动时，根据滑动距离计算缩放比例
            val progress = firstVisibleItemScrollOffset.toFloat() / contentHeight
            val disparity = (currentSelectedScale - currentUnselectedScale) * progress
            scrollingOutScale.value = currentSelectedScale - disparity
            scrollingInScale.value = currentUnselectedScale + disparity
            lastFirstVisibleItemScrollOffset = firstVisibleItemScrollOffset
            firstVisibleItemIndex = lastFirstVisibleItemIndex
        }.launchIn(this)

        var needReset = false
        var lastInteraction: Interaction? = null
        state.interactionSource.interactions.mapNotNull {
            it as? DragInteraction
        }.onEach { interaction ->
            // 滑动结束或取消时，判断是否需要复位
            val currentStart = (interaction as? DragInteraction.Stop)?.start
                ?: (interaction as? DragInteraction.Cancel)?.start
            needReset = currentStart == lastInteraction
        }.onEach { interaction ->
            lastInteraction = interaction
        }.launchIn(this)

        launch {
            snapshotFlow { state.isScrollInProgress }
                .filter { isScroll -> !isScroll && needReset }
                .onEach { needReset = false }
                .collectLatest {
                    val halfHeight = contentHeight / 2
                    if (lastFirstVisibleItemScrollOffset < halfHeight) {
                        // 若滑动距离小于一半，则回滚到上一个item
                        if (firstVisibleItemIndex < items.size) {
                            onItemSelected(
                                firstVisibleItemIndex,
                                items[firstVisibleItemIndex]
                            )
                        }
                        state.animateScrollToItem(firstVisibleItemIndex)
                    } else {
                        // 若滑动距离大于一半，则滚动到下一个item
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