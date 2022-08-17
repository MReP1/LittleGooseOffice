package little.goose.account.common.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import little.goose.account.logic.data.constant.KEY_DELETE_ITEM

typealias OnDeleteReceiver<T> = (context: Context, item: T) -> Unit

class DeleteItemBroadcastReceiver<T>(
    private val onDeleteReceiver: OnDeleteReceiver<T>
) : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        if (context != null && intent != null) {
            val item: T = intent.getParcelableExtra(KEY_DELETE_ITEM) ?: return
            onDeleteReceiver(context, item)
        }
    }

}