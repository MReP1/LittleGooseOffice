package little.goose.account.logic.data.models

import java.math.BigDecimal
import java.util.*

data class TransactionBalance(
    val time: Date = Date(),
    var expense: BigDecimal = BigDecimal(0),
    var income: BigDecimal = BigDecimal(0),
    var balance: BigDecimal = BigDecimal(0)
)
