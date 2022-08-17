package little.goose.account.utils

import android.widget.Toast
import little.goose.account.AccountApplication

object ToastUtils {
    fun showAny(msg: Any) {
        Toast.makeText(AccountApplication.context, msg.toString(), Toast.LENGTH_SHORT).show()
    }

    fun showString(msg: String) {
        Toast.makeText(AccountApplication.context, msg, Toast.LENGTH_SHORT).show()
    }
}