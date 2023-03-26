package little.goose.design.system.component.dialog

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable

@Stable
class DeleteDialogState {
    internal val dialogState: DialogState = DialogState(false)

    internal var onConfirm: (() -> Unit)? = null
    internal var onCancel: (() -> Unit)? = null

    fun show(
        onCancel: (() -> Unit)? = null,
        onConfirm: (() -> Unit)? = null
    ) {
        this.onConfirm = onConfirm
        this.onCancel = onCancel
        dialogState.show()
    }

    fun dismiss() {
        this.onConfirm = null
        this.onCancel = null
        dialogState.dismiss()
    }
}

@Composable
fun DeleteDialog(
    state: DeleteDialogState,
) {
    SelectorDialog(
        state = state.dialogState,
        onCancel = {
            state.onCancel?.invoke()
            state.dismiss()
        },
        onConfirm = {
            state.onConfirm?.invoke()
            state.dismiss()
        }
    ) {
        Text(text = "确认删除吗？")
    }
}