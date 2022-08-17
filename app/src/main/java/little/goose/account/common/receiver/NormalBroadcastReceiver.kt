package little.goose.account.common.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

typealias OnReceive = (context: Context, intent: Intent) -> Unit

class NormalBroadcastReceiver(
    private val onRec: OnReceive
) : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (context != null && intent != null) {
            onRec(context, intent)
        }
    }
}