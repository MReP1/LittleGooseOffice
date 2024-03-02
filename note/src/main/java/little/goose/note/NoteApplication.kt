package little.goose.note

import android.app.Application
import little.goose.note.di.sharedNoteFeatureModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import vibratorModule

class NoteApplication: Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@NoteApplication)
            modules(
                vibratorModule,
                sharedNoteFeatureModule
            )
        }
    }
}