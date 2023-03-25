package little.goose.design.system.component.dialog

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import little.goose.common.dialog.time.TimeType
import little.goose.design.system.component.TimeSelector
import little.goose.design.system.component.TimeSelectorState
import little.goose.design.system.theme.RoundedCorner24
import little.goose.design.system.theme.TopRoundedCorner24
import java.util.Date

@Composable
fun TimeSelectorCenterDialog(
    state: DialogState = rememberDialogState(),
    initTime: Date,
    type: TimeType,
    onConfirm: (Date) -> Unit
) {
    val scope = rememberCoroutineScope()
    val properties = remember { DialogProperties() }
    NormalDialog(state = state, properties = properties) {
        val selectorState = remember(initTime, type) { TimeSelectorState(initTime, type) }
        Surface(shape = RoundedCorner24) {
            TimeSelector(
                state = selectorState,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp),
                onConfirm = { time ->
                    scope.launch(Dispatchers.Main.immediate) {
                        onConfirm(time)
                        state.dismiss()
                    }
                }
            )
        }
    }
}

@Composable
fun TimeSelectorBottomDialog(
    state: BottomSheetDialogState = rememberBottomSheetDialogState(),
    initTime: Date,
    type: TimeType,
    onConfirm: (Date) -> Unit
) {
    val scope = rememberCoroutineScope()
    BottomSheetDialog(state = state) {
        Surface(shape = TopRoundedCorner24) {
            val selectorState = remember(initTime, type) { TimeSelectorState(initTime, type) }
            TimeSelector(
                state = selectorState,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp),
                onConfirm = { time ->
                    scope.launch(Dispatchers.Main.immediate) {
                        onConfirm(time)
                        state.close()
                    }
                }
            )
        }
    }
}