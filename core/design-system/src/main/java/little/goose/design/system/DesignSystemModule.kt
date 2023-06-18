package little.goose.design.system

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.dataStoreFile
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import little.goose.common.di.AppCoroutineScope
import little.goose.common.di.Dispatcher
import little.goose.common.di.GooseDispatchers
import little.goose.design.system.data.DesignPreference
import little.goose.design.system.data.serializer.DesignPreferenceSerializer
import little.goose.design.system.state.DesignSystemDataHolder
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DesignSystemModule {

    @Provides
    @Singleton
    fun provideDesignPreferenceDataStore(
        @ApplicationContext context: Context,
        @Dispatcher(GooseDispatchers.IO) ioDispatcher: CoroutineDispatcher,
        @AppCoroutineScope coroutineScope: CoroutineScope,
        designPreferenceSerializer: DesignPreferenceSerializer,
    ): DataStore<DesignPreference> {
        return DataStoreFactory.create(
            serializer = designPreferenceSerializer,
            scope = CoroutineScope(coroutineScope.coroutineContext + ioDispatcher),
        ) {
            context.dataStoreFile("design_preference.pb")
        }
    }

    @Provides
    @Singleton
    fun provideDesignSystemDataHolder(
        designPreference: DataStore<DesignPreference>
    ): DesignSystemDataHolder {
        return DesignSystemDataHolder(designPreference)
    }

}