package little.goose.account.logic.data.entities

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import little.goose.account.logic.data.constant.TABLE_MEMORIAL
import java.util.*

@Parcelize
@Entity(tableName = TABLE_MEMORIAL)
data class Memorial(
    @PrimaryKey(autoGenerate = true)
    var id: Long? = null,
    var content: String,
    var isTop: Boolean = false,
    var time: Date = Date()
) : Parcelable
