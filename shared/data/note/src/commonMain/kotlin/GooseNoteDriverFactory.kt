import app.cash.sqldelight.db.SqlDriver
import org.koin.core.module.Module

expect class GooseNoteDriverFactory {
    fun create(): SqlDriver
}

expect fun Module.noteDatabaseDriver()