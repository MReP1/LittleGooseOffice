package little.goose.common.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import little.goose.common.localBroadcastManager
import little.goose.common.registerDeleteReceiver

typealias OnReceive = (context: Context, intent: Intent) -> Unit

class NormalBroadcastReceiver : BroadcastReceiver() {

    private var onRec: OnReceive? = null

    override fun onReceive(context: Context?, intent: Intent?) {
        if (context != null && intent != null) {
            onRec?.invoke(context, intent)
        }
    }

    fun register(context: Context, lifecycle: Lifecycle, action: String, onRec: OnReceive) {
        this.onRec = onRec
        lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onResume(owner: LifecycleOwner) {
                super.onResume(owner)
                context.localBroadcastManager.registerDeleteReceiver(
                    action, this@NormalBroadcastReceiver
                )
            }

            override fun onPause(owner: LifecycleOwner) {
                super.onPause(owner)
                context.localBroadcastManager.unregisterReceiver(
                    this@NormalBroadcastReceiver
                )
            }
        })
    }
}