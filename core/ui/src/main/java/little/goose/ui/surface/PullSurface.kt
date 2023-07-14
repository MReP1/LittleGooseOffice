package little.goose.ui.surface

import android.os.VibrationEffect
import android.os.Vibrator
import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.rememberScrollableState
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import androidx.compose.ui.unit.min
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import little.goose.design.system.theme.AccountTheme
import little.goose.ui.modifier.nestedPull
import kotlin.math.max

@Composable
fun PullSurface(
    modifier: Modifier = Modifier,
    onPull: () -> Unit,
    surfaceColor: Color = MaterialTheme.colorScheme.background,
    backgroundContent: @Composable BoxScope.(progress: Float) -> Unit,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.TopCenter
    ) {
        val context = LocalContext.current
        val progress = remember { Animatable(0F) }
        val scope = rememberCoroutineScope()
        val vibrator = remember(context) {
            runCatching {
                context.getSystemService(Vibrator::class.java)
            }.getOrNull()
        }
        backgroundContent(progress.value)
        Surface(
            Modifier
                .fillMaxSize()
                .offset(y = max(64.dp * progress.value, 0.dp))
                .nestedPull(
                    threshold = 64.dp,
                    passThreshold = {
                        vibrator?.vibrate(
                            VibrationEffect.createOneShot(28L, 140)
                        )
                    },
                    onPull = { pullDelta, threshold ->
                        val newProgress = max(
                            progress.value + pullDelta / threshold, 0F
                        )
                        scope.launch(Dispatchers.Main.immediate) {
                            progress.snapTo(newProgress)
                        }
                        if (newProgress > 0) pullDelta else 0f
                    },
                    onRelease = { flingVelocity ->
                        // 取值可能会上锁，所以取一次存在本地复用
                        val p = progress.value
                        if (p == 1F || (p > 0.32F && (p + flingVelocity / 4711) > 1F)) {
                            vibrator?.vibrate(
                                VibrationEffect.createOneShot(36L, 180)
                            )
                            onPull()
                        }
                        scope.launch {
                            progress.animateTo(0F)
                        }
                    }
                ),
            color = surfaceColor,
            content = {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .scrollable(
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

@Preview
@Composable
fun PreviewPullSurface() = AccountTheme {
    PullSurface(
        onPull = { },
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
        }
    ) {
        Box(modifier = Modifier.fillMaxSize())
    }
}