package little.goose.note.di

import little.goose.note.ui.NotebookViewModel
import little.goose.note.ui.note.NoteViewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

val noteFeatureModule = module {
    includes(sharedNoteFeatureModule)
    viewModelOf(::NoteViewModel)
    viewModelOf(::NotebookViewModel)
}