package little.goose.account.utils

import android.content.IntentFilter
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import little.goose.account.appContext
import little.goose.account.common.receiver.DeleteItemBroadcastReceiver
import little.goose.account.logic.data.constant.NOTIFY_DELETE_TRANSACTION

val localBroadcastManager get() = LocalBroadcastManager.getInstance(appContext)

fun<T> LocalBroadcastManager.registerDeleteReceiver(action: String, receiver: DeleteItemBroadcastReceiver<T>) {
    val filter = IntentFilter().apply { addAction(action) }
    this.registerReceiver(receiver, filter)
}