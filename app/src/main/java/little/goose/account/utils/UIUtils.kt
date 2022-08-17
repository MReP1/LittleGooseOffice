package little.goose.account.utils

import android.view.ViewGroup
import little.goose.account.AccountApplication

object UIUtils {
    private val displayMetrics = AccountApplication.context.resources.displayMetrics

    private val density = displayMetrics.density

    fun dpToPxF(dp: Float): Float = dp * density * if (dp > 0) 1 else -1
    fun dpToPxF(dp: Int): Float = (dp * density * if (dp > 0) 1 else -1)
    fun dpToPx(dp: Int): Int = (dp * density * if (dp > 0) 1 else -1).toInt()
    fun dpToPx(dp: Float): Int = (dp * density * if (dp > 0) 1 else -1).toInt()

    fun getScreenWidth() = displayMetrics.widthPixels
    fun getScreenHeight() = displayMetrics.heightPixels

    fun getWidthPercentPixel(percent: Float) = when (percent) {
        0F -> ViewGroup.LayoutParams.WRAP_CONTENT
        1F -> ViewGroup.LayoutParams.MATCH_PARENT
        else -> (percent * getScreenWidth()).toInt()
    }
}

fun Int.dp(): Int = UIUtils.dpToPx(this)
fun Int.dpf() = UIUtils.dpToPxF(this)
fun Float.dp() = UIUtils.dpToPx(this)
fun Float.dpf() = UIUtils.dpToPxF(this)

val dp16 = 16.dp()