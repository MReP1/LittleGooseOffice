package little.goose.note

import little.goose.note.di.sharedNoteFeatureModule
import org.koin.core.context.startKoin
import vibratorModule

fun initKoin() {
    startKoin {
        modules(
            vibratorModule,
            sharedNoteFeatureModule
        )
    }
}