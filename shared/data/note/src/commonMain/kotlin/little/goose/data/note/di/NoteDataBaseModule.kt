package little.goose.data.note.di

import GooseNoteDriverFactory
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.SupervisorJob
import little.goose.note.GooseNoteDatabase
import noteDatabaseDriver
import org.koin.core.qualifier.named
import org.koin.dsl.module

val Quality_Coroutine_Note = named("note")

val noteDatabaseModule = module {

    single<CoroutineScope>(Quality_Coroutine_Note) {
        CoroutineScope(
            Dispatchers.IO + SupervisorJob() +
                    CoroutineExceptionHandler { _, throwable -> throwable.printStackTrace() }
        )
    }

    noteDatabaseDriver()

    single<GooseNoteDatabase> {
        val factory = get<GooseNoteDriverFactory>()
        GooseNoteDatabase(factory.create())
    }

}