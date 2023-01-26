package little.goose.account.common.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import little.goose.account.logic.data.constant.KEY_DELETE_ITEM
import little.goose.account.logic.data.constant.NOTIFY_DELETE_MEMORIAL
import little.goose.account.utils.localBroadcastManager
import little.goose.account.utils.registerDeleteReceiver

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
        lifecycle: Lifecycle,
        action: String,
        onDeleteReceiver: OnDeleteReceiver<T>
    ) {
        this.onDeleteReceiver = onDeleteReceiver
        lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onResume(owner: LifecycleOwner) {
                super.onResume(owner)
                localBroadcastManager.registerDeleteReceiver(
                    action, this@DeleteItemBroadcastReceiver
                )
            }

            override fun onPause(owner: LifecycleOwner) {
                super.onPause(owner)
                localBroadcastManager.unregisterReceiver(
                    this@DeleteItemBroadcastReceiver
                )
            }
        })
    }

}