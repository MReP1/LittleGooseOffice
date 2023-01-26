package little.goose.account.utils

import android.widget.Toast
import little.goose.account.AccountApplication
import little.goose.account.appContext

object ToastUtils {
    fun showAny(msg: Any) {
        Toast.makeText(appContext, msg.toString(), Toast.LENGTH_SHORT).show()
    }

    fun showString(msg: String) {
        Toast.makeText(appContext, msg, Toast.LENGTH_SHORT).show()
    }
}