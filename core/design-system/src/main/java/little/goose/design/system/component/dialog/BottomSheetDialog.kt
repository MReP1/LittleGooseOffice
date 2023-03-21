package little.goose.design.system.component.dialog

import android.view.Gravity
import android.view.View
import android.view.Window
import android.view.WindowManager.LayoutParams
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.DialogWindowProvider
import androidx.compose.ui.window.SecureFlagPolicy
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlin.math.max
import kotlin.math.min

enum class BottomSheetDialogValue {
    Closed, Closing, Opened, Opening
}

@Stable
class BottomSheetDialogState(
    initValue: BottomSheetDialogValue,
    internal val navigationBarHeight: Float,
    private val enterAnimationSpec: AnimationSpec<Float>,
    private val exitAnimationSpec: AnimationSpec<Float>
) {
    var state = mutableStateOf(initValue)

    internal var height = MutableStateFlow<Float?>(null)

    var offsetAnimatable = Animatable(-navigationBarHeight)

    val isShowing: Boolean
        get() = when (state.value) {
            BottomSheetDialogValue.Opening, BottomSheetDialogValue.Opened -> true
            else -> false
        }

    val isHiding
        get() = when (state.value) {
            BottomSheetDialogValue.Closing, BottomSheetDialogValue.Closed -> true
            else -> false
        }

    val isOpening get() = state.value == BottomSheetDialogValue.Opening

    val isOpened get() = state.value == BottomSheetDialogValue.Opened

    val isClosed get() = state.value == BottomSheetDialogValue.Closed

    val isClosing get() = state.value == BottomSheetDialogValue.Closing

    suspend fun open() {
        height.value = null
        if (state.value == BottomSheetDialogValue.Opening) return
        state.value = BottomSheetDialogValue.Opening
        offsetAnimatable.animateTo(
            targetValue = height.filterNotNull().first(),
            animationSpec = enterAnimationSpec
        )
        state.value = BottomSheetDialogValue.Opened
    }

    suspend fun close() {
        if (state.value == BottomSheetDialogValue.Closing) return
        state.value = BottomSheetDialogValue.Closing
        offsetAnimatable.animateTo(
            targetValue = -2 * navigationBarHeight,
            animationSpec = exitAnimationSpec
        )
        state.value = BottomSheetDialogValue.Closed
    }

    companion object {
        fun Saver(
            navigationBarHeight: Float,
            enterAnimationSpec: AnimationSpec<Float>,
            exitAnimationSpec: AnimationSpec<Float>
        ) = Saver<BottomSheetDialogState, BottomSheetDialogValue>(
            save = { state ->
                state.state.value
            },
            restore = { value ->
                BottomSheetDialogState(
                    value, navigationBarHeight, enterAnimationSpec, exitAnimationSpec
                )
            }
        )
    }
}

@Composable
fun rememberBottomSheetDialogState(
    initValue: BottomSheetDialogValue = BottomSheetDialogValue.Closed,
    enterAnimationSpec: AnimationSpec<Float> = getDefaultAnimationSpec(),
    exitAnimationSpec: AnimationSpec<Float> = getDefaultAnimationSpec()
): BottomSheetDialogState {
    val view = LocalView.current
    val navigationBarHeight = remember(view) {
        ViewCompat.getRootWindowInsets(view)
            ?.getInsets(WindowInsetsCompat.Type.navigationBars())
            ?.bottom?.toFloat() ?: 0F
    }
    return rememberSaveable(
        saver = BottomSheetDialogState.Saver(
            navigationBarHeight, enterAnimationSpec, exitAnimationSpec
        )
    ) {
        BottomSheetDialogState(
            initValue = initValue, navigationBarHeight, enterAnimationSpec, exitAnimationSpec
        )
    }
}

private fun getDefaultAnimationSpec(): AnimationSpec<Float> = tween(
    durationMillis = 256,
    delayMillis = 0,
    easing = FastOutSlowInEasing
)

@Stable
data class BottomSheetDialogProperties(
    val dismissOnBackPress: Boolean = true,
    val dismissOnClickOutside: Boolean = true,
    val isDraggable: Boolean = true,
    val securePolicy: SecureFlagPolicy = SecureFlagPolicy.Inherit
)

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun BottomSheetDialog(
    modifier: Modifier = Modifier,
    dialogProperties: BottomSheetDialogProperties = remember { BottomSheetDialogProperties() },
    state: BottomSheetDialogState = rememberBottomSheetDialogState(),
    onDismissRequest: (() -> Unit)? = null,
    content: @Composable (BoxScope.() -> Unit)
) {
    val scope = rememberCoroutineScope()
    if (!state.isClosed) {
        Dialog(
            onDismissRequest = onDismissRequest ?: {
                scope.launch(Dispatchers.Main.immediate) { state.close() }
                Unit
            },
            properties = remember {
                DialogProperties(
                    dismissOnBackPress = dialogProperties.dismissOnBackPress,
                    dismissOnClickOutside = dialogProperties.dismissOnClickOutside,
                    securePolicy = dialogProperties.securePolicy,
                    usePlatformDefaultWidth = false
                )
            }
        ) {
            // set window layout param.
            val dialogView = LocalView.current
            DisposableEffect(dialogView) {
                updateDialogWindowAttribute(dialogView) {
                    gravity = Gravity.BOTTOM
                }
                onDispose { }
            }

            // content
            Layout(
                content = {
                    Box(modifier = modifier.fillMaxWidth(), content = content)
                },
                modifier = if (!dialogProperties.isDraggable) {
                    Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                } else {
                    Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .draggable(
                            state = rememberDraggableState(
                                onDelta = { offset ->
                                    scope.launch(Dispatchers.Main.immediate) {
                                        val currentOffset = state.offsetAnimatable.value - offset
                                        state.offsetAnimatable.snapTo(
                                            min(
                                                max(
                                                    -state.navigationBarHeight,
                                                    currentOffset
                                                ),
                                                state.height.value ?: currentOffset
                                            )
                                        )
                                    }
                                }
                            ),
                            orientation = Orientation.Vertical,
                            onDragStopped = {
                                val height = state.height.value
                                    ?: run { state.close(); return@draggable }
                                if (state.offsetAnimatable.value > height / 2f) {
                                    state.open()
                                } else {
                                    state.close()
                                }
                            }
                        )
                }
            ) { measurables, constraints ->
                val placeable = measurables[0].measure(constraints)
                layout(placeable.width, placeable.height) {
                    val y = placeable.height - state.offsetAnimatable.value.toInt()
                    placeable.place(0, y)
                    if (state.height.value != placeable.height.toFloat() && state.isOpening) {
                        state.height.value = placeable.height.toFloat()
                    }
                }
            }
        }
    }
}

private inline fun updateDialogWindowAttribute(view: View, updater: LayoutParams.() -> Unit) {
    withDialogWindow(view) { attributes = attributes.apply(updater) }
}

private inline fun withDialogWindow(view: View, setter: Window.() -> Unit) {
    findDialogWindowProviderInParent(view)?.window?.apply(setter)
}

fun findDialogWindowProviderInParent(view: View?): DialogWindowProvider? =
    if (view is DialogWindowProvider) view else view?.parent?.let { parent ->
        findDialogWindowProviderInParent(parent as? View)
    }
