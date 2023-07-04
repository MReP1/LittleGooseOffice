package little.goose.ui.modifier

import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp

fun Modifier.nestedPull(
    onPull: (pullDelta: Float) -> Float,
    threshold: Dp = 64.dp,
    passThreshold: () -> Unit,
    onRelease: suspend (flingVelocity: Float) -> Unit,
    enabled: Boolean = true
) = composed {
    val density = LocalDensity.current
    val connection = remember(threshold) {
        PullNestedScrollConnection(
            with(density) { threshold.toPx() }, passThreshold, onPull, onRelease, enabled
        )
    }
    nestedScroll(connection)
}

class PullNestedScrollConnection(
    private val threshold: Float,
    private val passThreshold: () -> Unit,
    private val onPull: (pullDelta: Float) -> Float,
    private val onRelease: suspend (flingVelocity: Float) -> Unit,
    private val enabled: Boolean
) : NestedScrollConnection {

    private var isExceedThreshold = false

    private var offsetY = 0F

    override fun onPreScroll(
        available: Offset,
        source: NestedScrollSource
    ): Offset = when {
        !enabled -> Offset.Zero

        // 向上滑动，父布局先处理（收回偏移），走 onPull 回调，并根据处理结果返回被消费掉的 Offset
        source == NestedScrollSource.Drag && available.y < 0 -> {
            if (offsetY > 0) {
                handleAvailableOffset(available)
            } else {
                Offset.Zero
            }
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
        source == NestedScrollSource.Drag && available.y > 0 -> {
            handleAvailableOffset(available)
        }

        else -> Offset.Zero
    }

    private fun handleAvailableOffset(available: Offset): Offset {
        offsetY += available.y
        if (offsetY >= threshold && !isExceedThreshold) {
            isExceedThreshold = true
            passThreshold()
        } else if (offsetY < threshold && isExceedThreshold) {
            isExceedThreshold = false
            passThreshold()
        }
        return Offset(0f, onPull(available.y)) // Swiping up
    }

    override suspend fun onPreFling(available: Velocity): Velocity {
        isExceedThreshold = false
        offsetY = 0F
        onRelease(available.y)
        return Velocity.Zero
    }
}