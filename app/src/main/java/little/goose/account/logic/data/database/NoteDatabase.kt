package little.goose.account.logic.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import little.goose.account.logic.data.dao.NoteDao
import little.goose.account.logic.data.entities.Note

@Database(entities = [Note::class], version = 1, exportSchema = false)
@TypeConverters(CommonTypeConverters::class)
abstract class NoteDatabase: RoomDatabase() {
    abstract fun noteDao(): NoteDao
}