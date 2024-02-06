import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import little.goose.data.note.NoteDataConstants
import little.goose.note.GooseNoteDatabase
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf

actual class GooseNoteDriverFactory {
    actual fun create(): SqlDriver {
        return NativeSqliteDriver(
            schema = GooseNoteDatabase.Schema,
            name = NoteDataConstants.FILE_NAME_NOTE_DATABASE
        )
    }
}

actual fun Module.noteDatabaseDriver() {
    singleOf(::GooseNoteDriverFactory)
}