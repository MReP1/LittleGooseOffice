package little.goose.account.utils

import little.goose.account.logic.data.entities.Transaction
import java.math.BigDecimal
import java.util.*
import kotlin.collections.HashMap

private val calendar by lazy { Calendar.getInstance() }

fun Transaction.getNegative(): Transaction {
    val signal = this.money.signum()
    if (signal >= 0) {
        this.money = this.money.negate()
    }
    return this
}

fun Transaction.getAbs(): Transaction {
    this.money = this.money.abs()
    return this
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