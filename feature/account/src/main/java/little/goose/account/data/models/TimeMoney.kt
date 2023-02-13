package little.goose.account.data.models

import java.math.BigDecimal
import java.util.*

data class TimeMoney(
    val time: Date,
    var money: BigDecimal
)