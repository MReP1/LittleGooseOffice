package little.goose.office

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import little.goose.note.di.noteFeatureModule
import little.goose.office.di.jankStatsModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.dsl.module
import vibrator

@HiltAndroidApp
class AccountApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@AccountApplication)
            modules(
                module { vibrator() },
                jankStatsModule,
                noteFeatureModule
            )
        }
    }
}