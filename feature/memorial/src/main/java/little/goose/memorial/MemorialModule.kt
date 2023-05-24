package little.goose.memorial

import android.app.Application
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped
import dagger.hilt.components.SingletonComponent
import little.goose.memorial.logic.DeleteMemorialUseCase
import little.goose.memorial.logic.DeleteMemorialsUseCase
import little.goose.memorial.logic.GetAllMemorialFlowUseCase
import little.goose.memorial.logic.GetMemorialAtTopFlowUseCase
import little.goose.memorial.logic.GetMemorialFlowUseCase
import little.goose.memorial.logic.GetMemorialsByYearMonthFlowUseCase
import little.goose.memorial.logic.InsertMemorialUseCase
import little.goose.memorial.logic.InsertMemorialsUseCase
import little.goose.memorial.logic.MemorialRepository
import little.goose.memorial.logic.SearchMemorialByTextFlowUseCase
import little.goose.memorial.logic.UpdateMemorialUseCase
import little.goose.memorial.logic.UpdateMemorialsUseCase
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

@Module
@InstallIn(ViewModelComponent::class)
class MemorialViewModelModule {

    @Provides
    @ViewModelScoped
    fun provideInsertMemorialUseCase(
        memorialRepository: MemorialRepository
    ): InsertMemorialUseCase {
        return InsertMemorialUseCase(memorialRepository)
    }

    @Provides
    @ViewModelScoped
    fun provideInsertMemorialsUseCase(
        memorialRepository: MemorialRepository
    ): InsertMemorialsUseCase {
        return InsertMemorialsUseCase(memorialRepository)
    }

    @Provides
    @ViewModelScoped
    fun provideUpdateMemorialUseCase(
        memorialRepository: MemorialRepository
    ): UpdateMemorialUseCase {
        return UpdateMemorialUseCase(memorialRepository)
    }

    @Provides
    @ViewModelScoped
    fun provideUpdateMemorialsUseCase(
        memorialRepository: MemorialRepository
    ): UpdateMemorialsUseCase {
        return UpdateMemorialsUseCase(memorialRepository)
    }

    @Provides
    @ViewModelScoped
    fun provideDeleteMemorialUseCase(
        memorialRepository: MemorialRepository
    ): DeleteMemorialUseCase {
        return DeleteMemorialUseCase(memorialRepository)
    }

    @Provides
    @ViewModelScoped
    fun provideDeleteMemorialsUseCase(
        memorialRepository: MemorialRepository
    ): DeleteMemorialsUseCase {
        return DeleteMemorialsUseCase(memorialRepository)
    }

    @Provides
    @ViewModelScoped
    fun provideGetAllMemorialFlowUseCase(
        memorialRepository: MemorialRepository
    ): GetAllMemorialFlowUseCase {
        return GetAllMemorialFlowUseCase(memorialRepository)
    }

    @Provides
    @ViewModelScoped
    fun provideSearchMemorialByTextFlowUseCase(
        memorialRepository: MemorialRepository
    ): SearchMemorialByTextFlowUseCase {
        return SearchMemorialByTextFlowUseCase(memorialRepository)
    }

    @Provides
    @ViewModelScoped
    fun provideGetMemorialFlowUseCase(
        memorialRepository: MemorialRepository
    ): GetMemorialFlowUseCase {
        return GetMemorialFlowUseCase(memorialRepository)
    }

    @Provides
    @ViewModelScoped
    fun provideGetMemorialAtTopFlowUseCase(
        memorialRepository: MemorialRepository
    ): GetMemorialAtTopFlowUseCase {
        return GetMemorialAtTopFlowUseCase(memorialRepository)
    }

    @Provides
    @ViewModelScoped
    fun provideGetMemorialsByYearMonthFlowUseCase(
        memorialRepository: MemorialRepository
    ): GetMemorialsByYearMonthFlowUseCase {
        return GetMemorialsByYearMonthFlowUseCase(memorialRepository)
    }

}