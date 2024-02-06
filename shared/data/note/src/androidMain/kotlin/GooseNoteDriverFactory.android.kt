import android.content.Context
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import little.goose.data.note.NoteDataConstants
import little.goose.note.GooseNoteDatabase
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf

actual class GooseNoteDriverFactory(private val context: Context) {
    actual fun create(): SqlDriver {
        return AndroidSqliteDriver(
            context = context,
            schema = GooseNoteDatabase.Schema,
            name = NoteDataConstants.FILE_NAME_NOTE_DATABASE
        )
    }
}

actual fun Module.noteDatabaseDriver() {
    singleOf(::GooseNoteDriverFactory)
}