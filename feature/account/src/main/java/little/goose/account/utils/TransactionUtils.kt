package little.goose.account.utils

import little.goose.account.data.constants.AccountConstant
import little.goose.account.data.entities.Transaction
import little.goose.common.utils.getDate
import little.goose.common.utils.getMonth
import little.goose.common.utils.getRealDate
import little.goose.common.utils.getYear
import java.math.BigDecimal
import java.util.*
import kotlin.collections.HashMap

private val calendar by lazy { Calendar.getInstance() }

fun Transaction.getNegative(): Transaction {
    val signal = this.money.signum()
    if (signal >= 0) {
        return this.copy(money = this.money.negate())
    }
    return this
}

fun Transaction.getAbs(): Transaction {
    return this.copy(money = this.money.abs())
}

fun List<Transaction>.getMapDayMoney(): HashMap<Int, BigDecimal> {
    val map = HashMap<Int, BigDecimal>()
    for (transaction in this) {
        val day = transaction.time.getRealDate(calendar)
        if (!map.containsKey(day)) {
            map[day] = transaction.money
        } else {
            map[day] = map[day]?.plus(transaction.money) ?: BigDecimal(0)
        }
    }
    return map
}

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
                        AccountConstant.TIME,
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