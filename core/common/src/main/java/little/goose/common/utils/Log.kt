package little.goose.common.utils

import android.util.Log
import androidx.activity.ComponentActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner

fun log(msg: Any?) {
    Log.d("Leon", msg.toString())
}