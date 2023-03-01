package little.goose.design.system.component.dialog

import androidx.compose.runtime.Composable
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

@Composable
fun NormalDialog(
    state: DialogState,
    properties: DialogProperties = DialogProperties(),
    content: @Composable () -> Unit
) {
    if (state.isShow) {
        Dialog(
            onDismissRequest = state::dismiss,
            properties = properties,
            content = content
        )
    }
}