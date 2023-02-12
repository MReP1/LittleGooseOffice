package little.goose.common.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import little.goose.common.constants.KEY_DELETE_ITEM
import little.goose.common.localBroadcastManager
import little.goose.common.registerDeleteReceiver

typealias OnDeleteReceiver<T> = (context: Context, item: T) -> Unit

class DeleteItemBroadcastReceiver<T> : BroadcastReceiver() {

    private var onDeleteReceiver: OnDeleteReceiver<T>? = null

    override fun onReceive(context: Context?, intent: Intent?) {
        if (context != null && intent != null) {
            val item: T = intent.getParcelableExtra(KEY_DELETE_ITEM) ?: return
            onDeleteReceiver?.invoke(context, item)
        }
    }

    fun register(
        context: Context,
        lifecycle: Lifecycle,
        action: String,
        onDeleteReceiver: OnDeleteReceiver<T>
    ) {
        this.onDeleteReceiver = onDeleteReceiver
        lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onResume(owner: LifecycleOwner) {
                super.onResume(owner)
                context.localBroadcastManager.registerDeleteReceiver(
                    action, this@DeleteItemBroadcastReceiver
                )
            }

            override fun onPause(owner: LifecycleOwner) {
                super.onPause(owner)
                context.localBroadcastManager.unregisterReceiver(
                    this@DeleteItemBroadcastReceiver
                )
            }
        })
    }

    fun registerForever(context: Context, action: String, onDeleteReceiver: OnDeleteReceiver<T>) {
        this.onDeleteReceiver = onDeleteReceiver
        context.localBroadcastManager.registerDeleteReceiver(action, this)
    }

    fun unregisterReceiver(context: Context) {
        context.localBroadcastManager.unregisterReceiver(this)
    }

}