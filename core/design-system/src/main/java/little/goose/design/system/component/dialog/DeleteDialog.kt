package little.goose.design.system.component.dialog

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun DeleteDialog(
    state: DialogState,
    onCancel: (() -> Unit)? = null,
    onConfirm: (() -> Unit)? = null,
) {
    SelectorDialog(
        state = state,
        onCancel = onCancel,
        onConfirm = onConfirm
    ) {
        Text(text = "确认删除吗？")
    }
}