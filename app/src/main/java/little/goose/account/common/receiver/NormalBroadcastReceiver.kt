package little.goose.account.common.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import little.goose.account.utils.localBroadcastManager
import little.goose.account.utils.registerDeleteReceiver

typealias OnReceive = (context: Context, intent: Intent) -> Unit

class NormalBroadcastReceiver : BroadcastReceiver() {

    private var onRec: OnReceive? = null

    override fun onReceive(context: Context?, intent: Intent?) {
        if (context != null && intent != null) {
            onRec?.invoke(context, intent)
        }
    }

    fun register(lifecycle: Lifecycle, action: String, onRec: OnReceive) {
        this.onRec = onRec
        lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onResume(owner: LifecycleOwner) {
                super.onResume(owner)
                localBroadcastManager.registerDeleteReceiver(
                    action, this@NormalBroadcastReceiver
                )
            }

            override fun onPause(owner: LifecycleOwner) {
                super.onPause(owner)
                localBroadcastManager.unregisterReceiver(
                    this@NormalBroadcastReceiver
                )
            }
        })
    }
}