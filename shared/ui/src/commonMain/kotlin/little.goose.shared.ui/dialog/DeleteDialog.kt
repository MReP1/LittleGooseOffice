package little.goose.shared.ui.dialog

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import little.goose.resource.GooseRes
import org.jetbrains.compose.resources.stringResource

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
        },
        icon = {
            Icon(imageVector = Icons.Rounded.Delete, contentDescription = "Delete")
        },
        title = {
            Text(text = stringResource(GooseRes.string.confirm_delete))
        },
        text = {
            Text(text = stringResource(GooseRes.string.delete_description))
        }
    )
}