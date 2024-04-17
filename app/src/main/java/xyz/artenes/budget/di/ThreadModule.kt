package xyz.artenes.budget.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.Dispatchers
import kotlin.coroutines.CoroutineContext

@Module
@InstallIn(SingletonComponent::class)
class ThreadModule {

    @Provides
    fun providesIODispatcher(): CoroutineContext {
        return Dispatchers.IO
    }

}