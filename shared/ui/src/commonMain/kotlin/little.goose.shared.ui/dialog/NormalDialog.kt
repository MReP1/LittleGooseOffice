package little.goose.shared.ui.dialog

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

@Composable
fun NormalDialog(
    state: DialogState,
    properties: DialogProperties = remember { DialogProperties() },
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