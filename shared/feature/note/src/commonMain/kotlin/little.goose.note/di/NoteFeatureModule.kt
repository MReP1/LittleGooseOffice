package little.goose.note.di

import little.goose.data.note.di.noteDatabaseModule
import little.goose.note.NoteHomeScreenModel
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val noteFeatureModule = module {

    includes(noteDatabaseModule)

    factoryOf(::NoteHomeScreenModel)

}