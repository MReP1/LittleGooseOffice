package little.goose.note.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import little.goose.common.converters.CommonTypeConverters
import little.goose.note.data.dao.NoteDao
import little.goose.note.data.entities.Note
import little.goose.note.data.entities.NoteContentBlock

@Database(
    entities = [Note::class, NoteContentBlock::class],
    version = 4,
    exportSchema = false
)
@TypeConverters(CommonTypeConverters::class)
abstract class NoteDatabase : RoomDatabase() {

    companion object {
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // SQLite does not support dropping columns. Therefore, we have to create a new table
                // database.execSQL("ALTER TABLE `note` DROP COLUMN content")
            }
        }
        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // create note content block table
                database.execSQL(
                    """CREATE TABLE IF NOT EXISTS `note_content_block` 
                       (`id` INTEGER PRIMARY KEY AUTOINCREMENT, 
                        `note_id` INTEGER,
                        `content` TEXT NOT NULL)""".trimIndent()
                )
            }
        }
        val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // create note content block table
                database.execSQL(
                    """ALTER TABLE `note_content_block` 
                       ADD COLUMN `index` INTEGER NOT NULL DEFAULT 0""".trimIndent()
                )
            }
        }
    }

    abstract fun noteDao(): NoteDao

}