package little.goose.schedule.ui

import android.os.VibrationEffect
import android.os.Vibrator
import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import androidx.compose.ui.unit.min
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import little.goose.design.system.theme.AccountTheme
import little.goose.schedule.data.entities.Schedule
import little.goose.ui.modifier.nestedPull

@Composable
fun ScheduleHome(
    modifier: Modifier,
    scheduleColumnState: ScheduleColumnState,
    onNavigateToSearch: () -> Unit,
    onNavigateToScheduleDialog: (Long) -> Unit
) {
    ScheduleScreen(
        modifier = modifier.fillMaxSize(),
        scheduleColumnState = scheduleColumnState,
        onNavigateToSearch = onNavigateToSearch,
        onScheduleClick = { schedule ->
            schedule.id?.let { id -> onNavigateToScheduleDialog(id) }
        }
    )
}

@Composable
private fun ScheduleScreen(
    modifier: Modifier = Modifier,
    scheduleColumnState: ScheduleColumnState,
    onNavigateToSearch: () -> Unit,
    onScheduleClick: (Schedule) -> Unit
) {
    val context = LocalContext.current
    val progress = remember { Animatable(0F) }
    val scope = rememberCoroutineScope()
    val vibrator = remember(context) { context.getSystemService(Vibrator::class.java) }
    Box(modifier = modifier) {
        Icon(
            imageVector = Icons.Rounded.Search,
            contentDescription = "Search",
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 12.dp)
                .size(min(48.dp, 24.dp + 24.dp * progress.value))
                .alpha(kotlin.math.max(1F - progress.value, 0.62F))
        )
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .offset(y = max(64.dp * progress.value, 0.dp))
                .nestedPull(
                    threshold = 64.dp,
                    passThreshold = {
                        vibrator.vibrate(
                            VibrationEffect.createOneShot(28L, 140)
                        )
                    },
                    onPull = { pullDelta, threshold ->
                        val newProgress = kotlin.math.max(
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
                            vibrator.vibrate(
                                VibrationEffect.createOneShot(36L, 180)
                            )
                            onNavigateToSearch()
                        }
                        scope.launch {
                            progress.animateTo(0F)
                        }
                    }
                ),
            color = MaterialTheme.colorScheme.background
        ) {
            ScheduleColumn(
                modifier = Modifier.fillMaxSize(),
                state = scheduleColumnState,
                onScheduleClick = onScheduleClick
            )
        }
    }
}

@Preview
@Composable
private fun PreviewScheduleHome() = AccountTheme {
    ScheduleHome(
        modifier = Modifier.fillMaxSize(),
        scheduleColumnState = ScheduleColumnState(
            schedules = (1L..15L).map { id ->
                Schedule(id = id)
            }
        ),
        onNavigateToSearch = {},
        onNavigateToScheduleDialog = {}
    )
}