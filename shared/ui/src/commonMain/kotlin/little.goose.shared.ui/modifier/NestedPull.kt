package little.goose.shared.ui.modifier

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import kotlin.properties.Delegates

@Composable
fun Modifier.nestedPull(
    onPull: (pullDelta: Float, threshold: Float) -> Float,
    threshold: Dp = 64.dp,
    passThreshold: () -> Unit,
    onRelease: suspend (flingVelocity: Float) -> Float,
    reverseDirection: Boolean = false,
    enabled: Boolean = true
): Modifier {
    val density = LocalDensity.current
    val connection = remember(density, threshold, enabled, reverseDirection) {
        PullNestedScrollConnection(
            with(density) { threshold.toPx() },
            passThreshold, onPull, onRelease, reverseDirection, enabled
        )
    }
    return nestedScroll(connection)
}

class PullNestedScrollConnection(
    private val threshold: Float,
    private val passThreshold: () -> Unit,
    private val onPull: (pullDelta: Float, threshold: Float) -> Float,
    private val onRelease: suspend (flingVelocity: Float) -> Float,
    private val reverseDirection: Boolean,
    private val enabled: Boolean
) : NestedScrollConnection {

    private var isExceedThreshold = false

    private var offsetY by Delegates.vetoable(0F) { _, _, newValue -> newValue >= 0 }

    override fun onPreScroll(
        available: Offset,
        source: NestedScrollSource
    ): Offset = when {
        !enabled -> Offset.Zero

        // 向上滑动，父布局先处理（收回偏移），走 onPull 回调，并根据处理结果返回被消费掉的 Offset
        source == NestedScrollSource.Drag &&
                ((!reverseDirection && available.y < 0)
                        || (reverseDirection && available.y > 0)) -> {
            handleAvailableOffset(available)
        }

        else -> Offset.Zero
    }

    override fun onPostScroll(
        consumed: Offset,
        available: Offset,
        source: NestedScrollSource
    ): Offset = when {
        !enabled -> Offset.Zero

        // 向下滑动，如果子布局处理完了还有剩余（拉到顶了还往下拉），就展示偏移
        source == NestedScrollSource.Drag && (
                (!reverseDirection && available.y > 0)
                        || (reverseDirection && available.y < 0)
                ) -> {
            handleAvailableOffset(available)
        }

        else -> Offset.Zero
    }

    private fun handleAvailableOffset(available: Offset): Offset {
        val lastOffset = offsetY
        offsetY += if (!reverseDirection) available.y else -available.y
        when {
            offsetY >= threshold && !isExceedThreshold -> {
                isExceedThreshold = true
                passThreshold()
            }

            offsetY < threshold && isExceedThreshold -> {
                isExceedThreshold = false
                passThreshold()
            }

            (lastOffset >= 0 && offsetY < 0) || (lastOffset <= 0 && offsetY > 0) -> {
                isExceedThreshold = false
            }
        }
        return Offset(0f, onPull(available.y, threshold)) // Swiping up
    }

    override suspend fun onPreFling(available: Velocity): Velocity {
        isExceedThreshold = false
        offsetY = 0F
        return available.copy(y = onRelease(available.y))
    }
}