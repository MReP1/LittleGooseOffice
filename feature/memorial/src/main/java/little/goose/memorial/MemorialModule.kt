package little.goose.memorial

import android.app.Application
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import little.goose.memorial.logic.MemorialRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class MemorialModule {

    @Provides
    @Singleton
    fun provideMemorialRepository(application: Application): MemorialRepository {
        return MemorialRepository(application)
    }

}