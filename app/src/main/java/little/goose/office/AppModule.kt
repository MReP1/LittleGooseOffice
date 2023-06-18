package little.goose.office

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import little.goose.design.system.state.DesignSystemDataHolder
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Provides
    @Singleton
    fun provideAppStateHolder(designSystemDataHolder: DesignSystemDataHolder): AppDataHolder {
        return AppDataHolder(designSystemDataHolder)
    }

}