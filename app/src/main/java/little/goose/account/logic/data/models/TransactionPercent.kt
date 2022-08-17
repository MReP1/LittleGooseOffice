package little.goose.account.logic.data.models

import java.math.BigDecimal

data class TransactionPercent(
    val icon_id: Int,
    val content: String,
    var money: BigDecimal,
    var percent: Double = 0.0
)