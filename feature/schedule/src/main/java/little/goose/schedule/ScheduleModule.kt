package little.goose.schedule

import android.app.Application
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import little.goose.schedule.logic.ScheduleRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class ScheduleModule {

    @Provides
    @Singleton
    fun provideScheduleRepository(application: Application): ScheduleRepository {
        return ScheduleRepository(application)
    }

}