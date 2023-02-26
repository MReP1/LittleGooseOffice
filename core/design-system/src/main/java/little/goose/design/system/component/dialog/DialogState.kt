package little.goose.design.system.component.dialog

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable

@Composable
fun rememberDialogState() = rememberSaveable(
    saver = Saver(
        save = { it.isShow.value },
        restore = { DialogState(mutableStateOf(it)) }
    )
) { DialogState() }

@Stable
class DialogState(
    internal val isShow: MutableState<Boolean> = mutableStateOf(false)
) {

    fun show() {
        isShow.value = true
    }

    fun dismiss() {
        isShow.value = false
    }

}