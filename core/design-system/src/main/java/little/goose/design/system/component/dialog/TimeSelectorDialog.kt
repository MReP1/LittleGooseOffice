package little.goose.design.system.component.dialog

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import little.goose.common.utils.TimeType
import little.goose.design.system.component.TimeSelector
import little.goose.design.system.component.TimeSelectorState
import little.goose.design.system.theme.RoundedCorner24
import little.goose.shared.ui.dialog.DialogState
import little.goose.shared.ui.dialog.NormalDialog
import little.goose.shared.ui.dialog.rememberDialogState
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
        val selectorState = remember(initTime) { TimeSelectorState(initTime) }
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
                },
                timeType = type
            )
        }
    }
}

@Composable
fun TimeSelectorBottomSheet(
    modifier: Modifier = Modifier,
    onDismissRequest: () -> Unit,
    bottomSheetState: SheetState,
    initTime: Date,
    type: TimeType,
    onConfirm: (Date) -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        modifier = modifier,
        sheetState = bottomSheetState
    ) {
        val selectorState = remember(initTime) { TimeSelectorState(initTime) }
        TimeSelector(
            state = selectorState,
            modifier = Modifier
                .fillMaxWidth()
                .height(if (type.containTime()) 400.dp else 240.dp),
            onConfirm = { time ->
                onConfirm(time)
                onDismissRequest()
            },
            timeType = type
        )
    }
}