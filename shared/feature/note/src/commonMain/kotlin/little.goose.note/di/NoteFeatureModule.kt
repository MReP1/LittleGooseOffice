package little.goose.note.di

import little.goose.data.note.di.noteDatabaseModule
import little.goose.note.NoteScreenModel
import org.koin.dsl.module

val sharedNoteFeatureModule = module {

    includes(noteDatabaseModule)

    factory<NoteScreenModel> {
        NoteScreenModel(it[0], get(), get(), get(), get(), get(), get())
    }

}