package little.goose.search.di

import little.goose.search.note.SearchNoteViewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

val searchNoteModule = module {
    viewModelOf(::SearchNoteViewModel)
}