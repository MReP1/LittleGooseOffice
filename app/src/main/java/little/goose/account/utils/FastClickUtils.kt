package little.goose.account.utils

object FastClickUtils {
    private var lastClickTime = 0L
    fun isNotFastClick(minTime: Long = 200L): Boolean {
        val curTime = System.currentTimeMillis()
        return if (curTime - lastClickTime > minTime) {
            lastClickTime = curTime
            true
        } else {
            lastClickTime = curTime
            false
        }
    }
}