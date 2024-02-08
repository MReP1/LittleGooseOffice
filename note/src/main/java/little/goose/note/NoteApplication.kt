package little.goose.note

import android.app.Application
import little.goose.note.di.noteFeatureModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class NoteApplication: Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@NoteApplication)
            modules(
                noteFeatureModule
            )
        }
    }
}