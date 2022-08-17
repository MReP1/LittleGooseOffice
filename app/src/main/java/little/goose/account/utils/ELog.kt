package little.goose.account.utils

import android.util.Log

object ELog {

    fun debugLifeCycle(msg: String) {
        Log.d("debugLifeCycle", msg)
    }

    fun d(msg: Any?) {
        msg?.let {
            Log.d("LeonLog", it.toString())
        } ?: run {
            Log.d("LeonLog", "null")
        }
    }
}

fun log(msg: Any?) {
    ELog.d(msg)
}