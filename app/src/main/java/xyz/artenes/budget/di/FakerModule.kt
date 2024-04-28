package xyz.artenes.budget.di

import com.github.javafaker.Faker
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class FakerModule {

    @Singleton
    @Provides
    fun providesFaker(): Faker {
        return Faker()
    }

}