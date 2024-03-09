package little.goose.shared.ui.dialog

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import little.goose.resource.GooseRes
import org.jetbrains.compose.resources.stringResource

@Composable
fun SelectorDialog(
    state: DialogState,
    onCancel: () -> Unit,
    onConfirm: () -> Unit,
    title: (@Composable () -> Unit)? = null,
    icon: (@Composable () -> Unit)? = null,
    text: (@Composable () -> Unit)? = null
) {
    if (state.isShow) {
        AlertDialog(
            onDismissRequest = state::dismiss,
            confirmButton = {
                TextButton(
                    onClick = {
                        onConfirm()
                        state.dismiss()
                    }
                ) {
                    Text(text = stringResource(GooseRes.string.confirm))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        onCancel()
                        state.dismiss()
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.secondary.copy(
                            alpha = 0.58F
                        )
                    )
                ) {
                    Text(text = stringResource(GooseRes.string.cancel))
                }
            },
            text = text,
            icon = icon,
            title = title
        )
    }
}