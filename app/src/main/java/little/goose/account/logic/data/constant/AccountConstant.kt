package little.goose.account.logic.data.constant

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

object AccountConstant {
    const val EXPENSE = 0 //消费
    const val INCOME = 1 //收入
    const val TIME = 2 //时间
}

@Parcelize
enum class MoneyType: Parcelable {
    BALANCE, EXPENSE, INCOME
}