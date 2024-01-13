package little.goose.design.system.component.dialog

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import little.goose.design.system.R
import little.goose.design.system.theme.GooseTheme

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
            Text(text = stringResource(id = R.string.confirm_delete))
        },
        text = {
            Text(text = stringResource(id = R.string.delete_description))
        }
    )
}

@Preview
@Composable
private fun PreviewDeleteDialog() = GooseTheme {
    DeleteDialog(state = DeleteDialogState().apply { show() })
}