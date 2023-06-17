package little.goose.office

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import little.goose.design.system.state.DesignSystemStateHolder
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Provides
    @Singleton
    fun provideAppStateHolder(designSystemStateHolder: DesignSystemStateHolder): AppStateHolder {
        return AppStateHolder(designSystemStateHolder)
    }

}