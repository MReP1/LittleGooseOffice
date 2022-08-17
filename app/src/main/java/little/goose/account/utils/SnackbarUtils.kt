package little.goose.account.utils

import android.view.View
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import little.goose.account.R

object SnackbarUtils {
    fun showNormalMessage(view: View, msg: String, length: Int = 1000) {
        Snackbar.make(view, msg, length)
            .setAction(R.string.alright) {}
            .setActionTextColor(ContextCompat.getColor(view.context, R.color.cancel_button))
            .show()
    }
}

fun View.showSnackbar(
    msg: String,
    length: Int = 1000,
    actionMsg: String? = null,
    action: (() -> Unit)? = null,
) {
    val snackbar = Snackbar.make(this, msg, length)
        .setActionTextColor(ContextCompat.getColor(this.context, R.color.cancel_button))
    if (actionMsg != null && action != null) {
        snackbar.setAction(actionMsg) { action() }
    }
    snackbar.show()
}

fun View.showSnackbar(
    msgRes: Int,
    length: Int = 1000,
    actionMsgRes: Int? = null,
    action: (() -> Unit)? = null,
) {
    val msg = this.context.getString(msgRes)
    val snackbar = Snackbar.make(this, msg, length)
        .setActionTextColor(ContextCompat.getColor(this.context, R.color.cancel_button))
    if (actionMsgRes != null && action != null) {
        val actionMsg = this.context.getString(actionMsgRes)
        snackbar.setAction(actionMsg) { action() }
    }
    snackbar.show()
}

fun View.showDeleteSnackbar(action: (() -> Unit)? = null) {
    this.showSnackbar(R.string.deleted, 1000, R.string.undo, action)
}

/**
 * 默认的Snackbar只有4秒和10秒可调整
 * 使用协程delay来灵活调整时间
 * */
suspend fun SnackbarHostState.showSnackbar(
    message: String,
    actionLabel: String? = null,
    duration: Long = 1000
) {
    coroutineScope {
        launch {
            val withDismissAction = !actionLabel.isNullOrEmpty()
            this@showSnackbar.showSnackbar(
                message,
                actionLabel,
                withDismissAction,
                SnackbarDuration.Indefinite
            )
        }
        launch {
            delay(duration)
            this@showSnackbar.currentSnackbarData?.dismiss()
        }
    }
}