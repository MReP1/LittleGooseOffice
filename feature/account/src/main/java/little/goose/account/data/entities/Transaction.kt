package little.goose.account.data.entities

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import little.goose.account.data.constants.TABLE_TRANSACTION
import java.math.BigDecimal
import java.util.*

@Parcelize
@Entity(tableName = TABLE_TRANSACTION)
data class Transaction(
    @PrimaryKey(autoGenerate = true)
    val id: Long? = null,
    val type: Int = 0,
    var money: BigDecimal = BigDecimal(0),
    var content: String = "",
    var description: String = "",
    var time: Date = Date(),
    var icon_id: Int = 1
) : Parcelable
