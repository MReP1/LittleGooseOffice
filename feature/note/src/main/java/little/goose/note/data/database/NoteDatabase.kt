package little.goose.note.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import little.goose.note.data.dao.NoteDao
import little.goose.note.data.entities.Note
import little.goose.common.converters.CommonTypeConverters

@Database(entities = [Note::class], version = 1, exportSchema = false)
@TypeConverters(CommonTypeConverters::class)
abstract class NoteDatabase: RoomDatabase() {
    abstract fun noteDao(): NoteDao
}