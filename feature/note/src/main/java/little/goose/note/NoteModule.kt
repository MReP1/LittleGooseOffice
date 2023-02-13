package little.goose.note

import android.app.Application
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import little.goose.note.logic.NoteRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class NoteModule {

    @Provides
    @Singleton
    fun provideNoteRepository(application: Application): NoteRepository {
        return NoteRepository(application)
    }

}