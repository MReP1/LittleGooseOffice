package little.goose.note

import android.app.Application
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped
import dagger.hilt.components.SingletonComponent
import little.goose.note.logic.DeleteNoteContentBlockUseCase
import little.goose.note.logic.DeleteNotesAndItsBlocksUseCase
import little.goose.note.logic.DeleteNotesEventUseCase
import little.goose.note.logic.GetNoteFlowUseCase
import little.goose.note.logic.GetNoteWithContentMapFlowUseCase
import little.goose.note.logic.GetNoteWithContentsMapFlowUseCase
import little.goose.note.logic.InsertNoteContentBlockUseCase
import little.goose.note.logic.InsertNoteUseCase
import little.goose.note.logic.NoteRepository
import little.goose.note.logic.UpdateNoteContentBlockUseCase
import little.goose.note.logic.UpdateNoteContentBlocksUseCase
import little.goose.note.logic.UpdateNoteUseCase
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class NoteModule {

    @Provides
    @Singleton
    fun provideNoteRepository(application: Application): NoteRepository {
        return NoteRepository(application)
    }

    @Provides
    @Singleton
    fun provideDeleteNotesEventUseCase(
        noteRepository: NoteRepository
    ): DeleteNotesEventUseCase {
        return DeleteNotesEventUseCase(noteRepository)
    }

}

@Module
@InstallIn(ViewModelComponent::class)
class NoteViewModelModule {

    @Provides
    @ViewModelScoped
    fun provideInsertNoteUseCase(
        noteRepository: NoteRepository
    ): InsertNoteUseCase {
        return InsertNoteUseCase(noteRepository)
    }

    @Provides
    @ViewModelScoped
    fun provideGetNoteFlowUseCase(
        noteRepository: NoteRepository
    ): GetNoteFlowUseCase {
        return GetNoteFlowUseCase(noteRepository)
    }

    @Provides
    @ViewModelScoped
    fun provideUpdateNoteUseCase(
        noteRepository: NoteRepository
    ): UpdateNoteUseCase {
        return UpdateNoteUseCase(noteRepository)
    }

    @Provides
    @ViewModelScoped
    fun provideInsertNoteContentBlockUseCase(
        noteRepository: NoteRepository
    ): InsertNoteContentBlockUseCase {
        return InsertNoteContentBlockUseCase(noteRepository)
    }

    @Provides
    @ViewModelScoped
    fun provideGetNoteWithContentsMapFlowUseCase(
        noteRepository: NoteRepository
    ): GetNoteWithContentsMapFlowUseCase {
        return GetNoteWithContentsMapFlowUseCase(noteRepository)
    }

    @Provides
    @ViewModelScoped
    fun provideGetNoteWithContentMapFlowUseCase(
        noteRepository: NoteRepository
    ): GetNoteWithContentMapFlowUseCase {
        return GetNoteWithContentMapFlowUseCase(noteRepository)
    }

    @Provides
    @ViewModelScoped
    fun provideUpdateNoteContentBlockUseCase(
        noteRepository: NoteRepository
    ): UpdateNoteContentBlockUseCase {
        return UpdateNoteContentBlockUseCase(noteRepository)
    }

    @Provides
    @ViewModelScoped
    fun provideUpdateNoteContentBlocksUseCase(
        noteRepository: NoteRepository
    ): UpdateNoteContentBlocksUseCase {
        return UpdateNoteContentBlocksUseCase(noteRepository)
    }

    @Provides
    @ViewModelScoped
    fun provideDeleteNoteContentBlockUseCase(
        noteRepository: NoteRepository
    ): DeleteNoteContentBlockUseCase {
        return DeleteNoteContentBlockUseCase(noteRepository)
    }

    @Provides
    @ViewModelScoped
    fun provideDeleteNotesAndItsBlocksUseCase(
        noteRepository: NoteRepository
    ): DeleteNotesAndItsBlocksUseCase {
        return DeleteNotesAndItsBlocksUseCase(noteRepository)
    }

}