import android.util.Log

actual fun log(msg: Any?) {
    Log.d("Leon", msg.toString())
}