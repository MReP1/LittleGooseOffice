package little.goose.memorial.data.entities

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import little.goose.memorial.data.constants.TABLE_MEMORIAL
import java.util.*

@Parcelize
@Entity(tableName = TABLE_MEMORIAL)
data class Memorial(
    @PrimaryKey(autoGenerate = true)
    val id: Long? = null,
    val content: String,
    val isTop: Boolean = false,
    val time: Date = Date()
) : Parcelable
