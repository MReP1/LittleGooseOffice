package little.goose.schedule.data.entities

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import little.goose.schedule.data.constants.TABLE_SCHEDULE
import java.util.*

@Parcelize
@Entity(tableName = TABLE_SCHEDULE)
data class Schedule(
    @PrimaryKey(autoGenerate = true)
    val id: Long? = null,
    val title: String = "",
    val content: String = "",
    val time: Date = Date(),
    val isfinish: Boolean = false
) : Parcelable
