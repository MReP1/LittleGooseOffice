package little.goose.home.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import little.goose.common.di.AppCoroutineScope
import little.goose.home.logic.HomePageDataHolder

private val Context.homeDataStore: DataStore<Preferences> by preferencesDataStore("home")
internal val KEY_PREF_PAGER = intPreferencesKey("pager")

@Module
@InstallIn(SingletonComponent::class)
class HomeModule {

    @Provides
    fun provideHomeDataStore(
        @ApplicationContext context: Context
    ): DataStore<Preferences> {
        return context.homeDataStore
    }

    @Provides
    fun provideHomePageDataHolder(
        homeDataStore: DataStore<Preferences>,
        @AppCoroutineScope coroutineScope: CoroutineScope
    ): HomePageDataHolder {
        return HomePageDataHolder(homeDataStore, coroutineScope)
    }

}