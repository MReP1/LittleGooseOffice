package little.goose.design.system.component.dialog

import androidx.compose.runtime.Composable
import androidx.compose.ui.window.Dialog

@Composable
fun NormalDialog(
    state: DialogState,
    content: @Composable () -> Unit
) {
    if (state.isShow.value) {
        Dialog(
            onDismissRequest = state::dismiss,
            content = content
        )
    }
}