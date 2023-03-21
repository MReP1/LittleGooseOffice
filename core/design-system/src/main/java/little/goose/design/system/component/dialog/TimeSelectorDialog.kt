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
import little.goose.design.system.component.TimeSelector
import little.goose.design.system.component.TimeSelectorState
import little.goose.design.system.theme.RoundedCorner24
import little.goose.design.system.theme.TopRoundedCorner24
import java.util.Date

@Composable
fun TimeSelectorDialog(
    state: DialogState = rememberDialogState(),
    initTime: Date,
    onConfirm: (Date) -> Unit
) {
    val scope = rememberCoroutineScope()
    val properties = remember { DialogProperties() }
    val selectorState = remember { TimeSelectorState(initTime) }
    NormalDialog(state = state, properties = properties) {
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
fun BottomSelectorDialog(
    state: BottomSheetDialogState = rememberBottomSheetDialogState(),
    initTime: Date,
    onConfirm: (Date) -> Unit
) {
    val scope = rememberCoroutineScope()
    BottomSheetDialog(state = state) {
        Surface(shape = TopRoundedCorner24) {
            val selectorState = remember(initTime) { TimeSelectorState(initTime) }
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