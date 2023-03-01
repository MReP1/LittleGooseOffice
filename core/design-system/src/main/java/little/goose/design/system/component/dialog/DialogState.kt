package little.goose.design.system.component.dialog

import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable

@Composable
fun rememberDialogState() = rememberSaveable(
    saver = Saver(
        save = { it.isShow },
        restore = { DialogState(it) }
    )
) { DialogState(false) }

@Stable
class DialogState(
    _isShow: Boolean
) {

    var isShow by mutableStateOf(_isShow)
        private set

    fun show() {
        isShow = true
    }

    fun dismiss() {
        isShow = false
    }

}