package little.goose.shared.ui.dialog

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue

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