package xyz.artenes.budget.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import xyz.artenes.budget.android.AndroidMessages
import xyz.artenes.budget.android.AndroidUserPreferences
import xyz.artenes.budget.core.Messages
import xyz.artenes.budget.core.UserPreferences
import javax.inject.Singleton
import kotlin.coroutines.CoroutineContext

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "preferences")

@Module
@InstallIn(SingletonComponent::class)
class AndroidModule {

    @Provides
    fun providesDataStore(@ApplicationContext context: Context): DataStore<Preferences> {
        return context.dataStore
    }

    @Provides
    @Singleton
    fun providesMessages(@ApplicationContext context: Context): Messages {
        return AndroidMessages(context)
    }

    @Provides
    @Singleton
    fun providesUserPreferences(
        dataStore: DataStore<Preferences>,
        coroutineContext: CoroutineContext
    ): UserPreferences {
        return AndroidUserPreferences(dataStore, coroutineContext)
    }

}