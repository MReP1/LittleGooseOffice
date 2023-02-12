package little.goose.common.utils

import android.view.View
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import little.goose.common.R

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