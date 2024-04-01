package little.goose.note.di

import little.goose.data.note.di.noteDatabaseModule
import little.goose.note.NoteScreenModel
import little.goose.shared.common.commonViewModelFactory
import org.koin.dsl.module

val sharedNoteFeatureModule = module {

    includes(noteDatabaseModule)

    commonViewModelFactory<NoteScreenModel> {
        NoteScreenModel(it[0], get(), get(), get(), get(), get(), get())
    }

}