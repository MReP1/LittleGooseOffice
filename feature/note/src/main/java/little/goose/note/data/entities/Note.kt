package little.goose.note.data.entities

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import little.goose.note.data.constants.TABLE_NOTE
import java.util.*

@Parcelize
@Entity(tableName = TABLE_NOTE)
data class Note(
    @PrimaryKey(autoGenerate = true)
    val id: Long? = null,
    @ColumnInfo("title")
    val title: String = "",
    @ColumnInfo("time")
    val time: Date = Date(),
    @ColumnInfo("content")
    val content: String = ""
) : Parcelable
