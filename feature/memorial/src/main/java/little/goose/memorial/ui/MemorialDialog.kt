package little.goose.memorial.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import little.goose.design.system.component.dialog.*
import little.goose.memorial.R
import little.goose.memorial.data.entities.Memorial
import little.goose.memorial.ui.component.MemorialCard

@Composable
fun rememberMemorialDialogState(): MemorialDialogState {
    return remember { MemorialDialogState() }
}

@Stable
class MemorialDialogState {

    var memorial by mutableStateOf(Memorial())

    internal val dialogState: DialogState = DialogState(false)

    fun show(memorial: Memorial) {
        this.memorial = memorial
        dialogState.show()
    }

    fun dismiss() {
        dialogState.dismiss()
    }

}

@Composable
fun MemorialDialog(
    state: MemorialDialogState,
    onNavigateToMemorialShow: (Long) -> Unit,
    onDelete: (Memorial) -> Unit
) {
    val deleteMemorialDialogState = remember { DeleteDialogState() }
    NormalDialog(state = state.dialogState) {
        MaterialDialogScreen(
            modifier = Modifier.wrapContentSize(),
            memorial = state.memorial,
            onDelete = {
                deleteMemorialDialogState.show(onConfirm = {
                    onDelete(state.memorial)
                    state.dismiss()
                })
            },
            onEdit = {
                state.memorial.id?.let { onNavigateToMemorialShow(it) }
                state.dismiss()
            }
        )
    }
    DeleteDialog(state = deleteMemorialDialogState)
}

@Composable
fun MaterialDialogScreen(
    modifier: Modifier,
    memorial: Memorial,
    onDelete: () -> Unit,
    onEdit: () -> Unit
) {
    Column(modifier.clip(RoundedCornerShape(24.dp))) {
        MemorialCard(
            memorial = memorial,
            shape = RectangleShape
        )
        Row(modifier.fillMaxWidth()) {
            Button(
                modifier = Modifier
                    .weight(1F)
                    .height(56.dp),
                onClick = onDelete,
                shape = RectangleShape
            ) {
                Text(text = stringResource(id = R.string.delete))
            }
            Button(
                modifier = Modifier
                    .weight(1F)
                    .height(56.dp),
                onClick = onEdit,
                shape = RectangleShape
            ) {
                Text(text = stringResource(id = R.string.edit))
            }
        }
    }
}