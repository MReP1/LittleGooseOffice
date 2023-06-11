package little.goose.schedule

import android.app.Application
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped
import dagger.hilt.components.SingletonComponent
import little.goose.schedule.logic.DeleteSchedulesUseCase
import little.goose.schedule.logic.GetAllScheduleFlowUseCase
import little.goose.schedule.logic.GetScheduleByIdFlowUseCase
import little.goose.schedule.logic.GetScheduleByYearMonthFlowUseCase
import little.goose.schedule.logic.InsertScheduleUseCase
import little.goose.schedule.logic.InsertSchedulesUseCase
import little.goose.schedule.logic.ScheduleRepository
import little.goose.schedule.logic.SearchScheduleByTextFlowUseCase
import little.goose.schedule.logic.UpdateScheduleUseCase
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class ScheduleModule {

    @Provides
    @Singleton
    fun provideScheduleRepository(application: Application): ScheduleRepository {
        return ScheduleRepository(application)
    }

    @Provides
    @Singleton
    fun provideDeleteSchedulesUseCase(
        scheduleRepository: ScheduleRepository
    ): DeleteSchedulesUseCase {
        return DeleteSchedulesUseCase(scheduleRepository)
    }

}

@Module
@InstallIn(ViewModelComponent::class)
class ScheduleViewModelModule {

    @Provides
    @ViewModelScoped
    fun provideInsertScheduleUseCase(
        scheduleRepository: ScheduleRepository
    ): InsertScheduleUseCase {
        return InsertScheduleUseCase(scheduleRepository)
    }

    @Provides
    @ViewModelScoped
    fun provideInsertSchedulesUseCase(
        scheduleRepository: ScheduleRepository
    ): InsertSchedulesUseCase {
        return InsertSchedulesUseCase(scheduleRepository)
    }

    @Provides
    @ViewModelScoped
    fun provideUpdateScheduleUseCase(
        scheduleRepository: ScheduleRepository
    ): UpdateScheduleUseCase {
        return UpdateScheduleUseCase(scheduleRepository)
    }

    @Provides
    @ViewModelScoped
    fun provideGetScheduleByIdFlowUseCase(
        scheduleRepository: ScheduleRepository
    ): GetScheduleByIdFlowUseCase {
        return GetScheduleByIdFlowUseCase(scheduleRepository)
    }

    @Provides
    @ViewModelScoped
    fun provideGetAllScheduleFlowUseCase(
        scheduleRepository: ScheduleRepository
    ): GetAllScheduleFlowUseCase {
        return GetAllScheduleFlowUseCase(scheduleRepository)
    }

    @Provides
    @ViewModelScoped
    fun provideGetScheduleByYearMonthFlowUseCase(
        scheduleRepository: ScheduleRepository
    ): GetScheduleByYearMonthFlowUseCase {
        return GetScheduleByYearMonthFlowUseCase(scheduleRepository)
    }

    @Provides
    @ViewModelScoped
    fun provideSearchScheduleByTextFlowUseCase(
        scheduleRepository: ScheduleRepository
    ): SearchScheduleByTextFlowUseCase {
        return SearchScheduleByTextFlowUseCase(scheduleRepository)
    }

}