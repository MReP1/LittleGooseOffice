package little.goose.design.system.component

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onPlaced
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.*

@Stable
class MovableActionButtonState {

    private val _isExpended = mutableStateOf(false)
    val isExpended: State<Boolean> get() = _isExpended

    internal val elevation = mutableStateOf(0.dp)

    internal val offset = Animatable(Offset(0F, 0F), Offset.VectorConverter)
    internal val topButtonOffset = Animatable(IntOffset(0, 0), IntOffset.VectorConverter)
    internal val bottomButtonOffset = Animatable(IntOffset(0, 0), IntOffset.VectorConverter)

    private var isPositioned = false

    internal var minOffsetX = 0F
    internal var maxOffsetX = 0F
    internal var minOffsetY = 0F
    internal var maxOffsetY = 0F

    private var shortDis = 0
    private var longDis = 0
    private var radius = 0
        set(value) {
            shortDis = value / 4
            longDis = value * 5 / 6
            field = value
        }

    suspend fun expend() {
        waitForInit()
        _isExpended.value = true
        coroutineScope {
            elevation.value = 6.dp
            awaitAll(
                async { topButtonOffset.animateTo(IntOffset(-shortDis, -longDis)) },
                async { bottomButtonOffset.animateTo(IntOffset(-longDis, -shortDis)) }
            )
        }
    }

    suspend fun fold() {
        waitForInit()
        _isExpended.value = false
        coroutineScope {
            awaitAll(
                async { topButtonOffset.animateTo(IntOffset(0, 0)) },
                async { bottomButtonOffset.animateTo(IntOffset(0, 0)) }
            )
            elevation.value = 0.dp
        }
    }

    private suspend fun waitForInit() {
        if (radius == 0) {
            while (true) {
                delay(16)
                if (radius != 0) break
            }
        }
    }

    suspend fun offsetReset() {
        offset.animateTo(
            targetValue = Offset.Zero,
            animationSpec = tween(140, 0, FastOutLinearInEasing)
        )
    }

    internal suspend fun offsetSnapTo(targetOffset: Offset) {
        offset.snapTo(targetOffset)
    }

    internal fun initLayout(
        layoutCoordinates: LayoutCoordinates,
        density: Density
    ) {
        if (isPositioned) return
        val size = layoutCoordinates.size
        val parentSize = layoutCoordinates.parentCoordinates?.size ?: IntSize(0, 0)
        radius = with(density) { (56.dp + 18.dp).roundToPx() }
        minOffsetX = -layoutCoordinates.positionInParent().x
        minOffsetY = -layoutCoordinates.positionInParent().y
        maxOffsetX = parentSize.width - size.width - layoutCoordinates.positionInParent().x
        maxOffsetY = parentSize.height - size.height - layoutCoordinates.positionInParent().y
        isPositioned = true
    }

}

@Composable
fun MovableActionButton(
    modifier: Modifier = Modifier,
    state: MovableActionButtonState,
    mainButtonContent: @Composable (isExpended: Boolean) -> Unit,
    onMainButtonClick: () -> Unit,
    topSubButtonContent: @Composable () -> Unit,
    onTopSubButtonClick: () -> Unit,
    bottomSubButtonContent: @Composable () -> Unit,
    onBottomSubButtonClick: () -> Unit
) {
    val density = LocalDensity.current
    val scope = rememberCoroutineScope()

    Box(
        modifier = modifier
            .onPlaced {
                state.initLayout(it, density)
            }
            .offset {
                IntOffset(state.offset.value.x.toInt(), state.offset.value.y.toInt())
            }
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = {
                        scope.launch(Dispatchers.Main.immediate) {
                            state.fold()
                        }
                    },
                    onDragEnd = {
                        scope.launch(Dispatchers.Main) {
                            state.offsetReset()
                        }
                    },
                    onDrag = { _, dragAmount ->
                        scope.launch(Dispatchers.Main.immediate) {
                            val x = (dragAmount.x + state.offset.value.x)
                                .coerceIn(state.minOffsetX, state.maxOffsetX)
                            val y = (dragAmount.y + state.offset.value.y)
                                .coerceIn(state.minOffsetY, state.maxOffsetY)
                            state.offsetSnapTo(Offset(x, y))
                        }
                    }
                )
            }
    ) {
        Box(
            modifier = Modifier.padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            FloatingActionButton(
                modifier = Modifier
                    .size(44.dp)
                    .offset { state.topButtonOffset.value },
                shape = CircleShape,
                onClick = {
                    scope.launch(Dispatchers.Main.immediate) {
                        onTopSubButtonClick()
                        state.fold()
                    }
                },
                content = topSubButtonContent,
                elevation = FloatingActionButtonDefaults.elevation(
                    defaultElevation = state.elevation.value
                )
            )

            FloatingActionButton(
                modifier = Modifier
                    .size(44.dp)
                    .offset { state.bottomButtonOffset.value },
                shape = CircleShape,
                onClick = {
                    scope.launch(Dispatchers.Main.immediate) {
                        onBottomSubButtonClick()
                        state.fold()
                    }
                },
                content = bottomSubButtonContent,
                elevation = FloatingActionButtonDefaults.elevation(
                    defaultElevation = state.elevation.value
                )
            )

            FloatingActionButton(
                modifier = Modifier.size(56.dp),
                onClick = {
                    scope.launch(Dispatchers.Main.immediate) {
                        if (state.isExpended.value) {
                            onMainButtonClick()
                            state.fold()
                        } else {
                            state.expend()
                        }
                    }
                },
                content = {
                    mainButtonContent(state.isExpended.value)
                }
            )
        }
    }
}