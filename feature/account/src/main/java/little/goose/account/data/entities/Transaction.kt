package little.goose.account.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import little.goose.account.data.constants.TABLE_TRANSACTION
import java.math.BigDecimal
import java.util.*

@Entity(tableName = TABLE_TRANSACTION)
data class Transaction(
    @PrimaryKey(autoGenerate = true)
    val id: Long? = null,
    val type: Int = 0,
    val money: BigDecimal = BigDecimal(0),
    val content: String = "",
    val description: String = "",
    val time: Date = Date(),
    val icon_id: Int = 1
)
