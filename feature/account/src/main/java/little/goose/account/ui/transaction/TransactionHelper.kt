package little.goose.account.ui.transaction

import little.goose.account.data.constants.AccountConstant.TIME
import little.goose.account.data.entities.Transaction
import little.goose.common.utils.getDate
import little.goose.common.utils.getMonth
import little.goose.common.utils.getYear
import java.math.BigDecimal
import java.util.*

fun List<Transaction>.insertTime(): List<Transaction> {
    val arrayList = ArrayList<Transaction>()
    var timeString = ""
    if (this.isEmpty()) {
        return emptyList()
    }
    Calendar.getInstance().apply {
        for (transaction in this@insertTime) {
            time = transaction.time
            val year = getYear()
            val month = getMonth()
            val date = getDate()
            val tempTime = "${year}年${month}月${date}日"
            val monthDayTime = "${month}月${date}日"
            if (tempTime != timeString) {
                arrayList.add(
                    Transaction(
                        null,
                        TIME,
                        BigDecimal(0),
                        "",
                        monthDayTime,
                        Date(),
                        1
                    )
                )
                timeString = tempTime
            }
            arrayList.add(transaction)
        }
    }
    return arrayList
}