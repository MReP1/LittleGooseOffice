package little.goose.common.utils

import android.util.Log
import androidx.activity.ComponentActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner

fun log(msg: Any?) {
    Log.d("Leon", msg.toString())
}

fun Fragment.debugLifeCycle() {
    lifecycle.addObserver(object : DefaultLifecycleObserver {
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

fun ComponentActivity.debugLifeCycle() {
    lifecycle.addObserver(object : DefaultLifecycleObserver {
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

private fun ComponentActivity.debugLifeCycle(message: String) {
    Log.d("debugLifeCycle", this@debugLifeCycle::class.simpleName.toString() + message)
}

private fun Fragment.debugLifeCycle(message: String) {
    Log.d("debugLifeCycle", this@debugLifeCycle::class.simpleName.toString() + message)
}