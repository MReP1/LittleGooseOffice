package little.goose.shared.ui.surface

import Vibration
import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.rememberScrollableState
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import androidx.compose.ui.unit.min
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import little.goose.shared.ui.modifier.nestedPull
import rememberVibrator

@Composable
fun PullSurface(
    modifier: Modifier = Modifier,
    onPull: () -> Unit,
    surfaceColor: Color = MaterialTheme.colorScheme.background,
    backgroundContent: @Composable BoxScope.(progress: Float) -> Unit,
    reverseDirection: Boolean = false,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier.clipToBounds(),
        contentAlignment = if (!reverseDirection) {
            Alignment.TopCenter
        } else {
            Alignment.BottomCenter
        }
    ) {
        val progress = remember { Animatable(0F) }
        val scope = rememberCoroutineScope()
        val vibrator = rememberVibrator()

        backgroundContent(if (!reverseDirection) progress.value else -progress.value)

        Surface(
            Modifier
                .offset(
                    y = if (!reverseDirection) {
                        max(64.dp * progress.value, 0.dp)
                    } else {
                        min(64.dp * progress.value, 0.dp)
                    }
                )
                .nestedPull(
                    threshold = 64.dp,
                    passThreshold = {
                        vibrator.vibrate(Vibration.OneShot(28L, 166))
                    },
                    onPull = { pullDelta, threshold ->
                        val newProgress = if (!reverseDirection) kotlin.math.max(
                            progress.value + pullDelta / threshold, 0F
                        ) else kotlin.math.min(
                            progress.value + pullDelta / threshold, 0F
                        )
                        scope.launch(Dispatchers.Main.immediate) {
                            progress.snapTo(newProgress)
                        }
                        if ((!reverseDirection && newProgress > 0)
                            || (reverseDirection && newProgress < 0)
                        ) pullDelta else 0f
                    },
                    reverseDirection = reverseDirection,
                    onRelease = { flingVelocity ->
                        // 取值可能会上锁，所以取一次存在本地复用
                        val p = progress.value
                        if (
                            (!reverseDirection && p >= 1F || (p > 0.32F && (p + flingVelocity / 4711) > 1F))
                            || (reverseDirection && p <= -1F || (p < -0.32F && (p + flingVelocity / 4711) < -1F))
                        ) {
                            vibrator.vibrate(Vibration.OneShot(36L, 180))
                            onPull()
                        }
                        scope.launch {
                            progress.animateTo(0F)
                        }
                        if (!reverseDirection && p > 0) flingVelocity
                        else if (reverseDirection && p < 0) -flingVelocity
                        else 0F
                    }
                ),
            color = surfaceColor,
            content = {
                Box(
                    modifier = Modifier.scrollable(
                        rememberScrollableState(consumeScrollDelta = { 0F }),
                        Orientation.Vertical
                    )
                ) {
                    content()
                }
            }
        )
    }
}