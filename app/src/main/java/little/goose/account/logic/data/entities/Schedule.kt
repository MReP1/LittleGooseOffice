package little.goose.account.logic.data.entities

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import little.goose.account.logic.data.constant.TABLE_SCHEDULE
import java.util.*

@Parcelize
@Entity(tableName = TABLE_SCHEDULE)
data class Schedule(
    @PrimaryKey(autoGenerate = true)
    val id: Long? = null,
    var title: String,
    var content: String,
    var time: Date = Date(),
    var isfinish: Boolean = false
) : Parcelable
