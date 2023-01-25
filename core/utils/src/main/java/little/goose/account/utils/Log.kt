package little.goose.account.utils

import android.util.Log
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner

fun log(msg: Any?) {
    Log.d("Leon", msg.toString())
}

fun Lifecycle.debugLifeCycle() {
    addObserver(object : DefaultLifecycleObserver {
        override fun onCreate(owner: LifecycleOwner) {
            super.onCreate(owner)
            debugLifeCycle(" - onCreate")
        }

        override fun onStart(owner: LifecycleOwner) {
            super.onStart(owner)
            debugLifeCycle(" - onStart")
        }

        override fun onResume(owner: LifecycleOwner) {
            super.onResume(owner)
            debugLifeCycle(" - onResume")
        }

        override fun onPause(owner: LifecycleOwner) {
            super.onPause(owner)
            debugLifeCycle(" - onPause")
        }

        override fun onStop(owner: LifecycleOwner) {
            super.onStop(owner)
            debugLifeCycle(" - onStop")
        }

        override fun onDestroy(owner: LifecycleOwner) {
            super.onDestroy(owner)
            debugLifeCycle(" - onDestroy")
        }
    })
}

fun Lifecycle.debugLifeCycle(message: String) {
    Log.d("debugLifeCycle", this@debugLifeCycle::class.simpleName.toString() + message)
}