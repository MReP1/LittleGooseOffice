package little.goose.common

import android.content.BroadcastReceiver
import android.content.Context
import android.content.IntentFilter
import androidx.localbroadcastmanager.content.LocalBroadcastManager

//FIXME
val Context.localBroadcastManager get() = LocalBroadcastManager.getInstance(this)

fun LocalBroadcastManager.registerDeleteReceiver(
    action: String,
    receiver: BroadcastReceiver
) {
    val filter = IntentFilter().apply { addAction(action) }
    this.registerReceiver(receiver, filter)
}