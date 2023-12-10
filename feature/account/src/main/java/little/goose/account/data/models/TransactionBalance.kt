package little.goose.account.data.models

import androidx.compose.runtime.Stable
import java.math.BigDecimal
import java.util.Date

@Stable
data class TransactionBalance(
    val time: Date = Date(),
    var expense: BigDecimal = BigDecimal(0),
    var income: BigDecimal = BigDecimal(0),
    var balance: BigDecimal = BigDecimal(0)
)
