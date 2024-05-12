package xyz.artenes.budget.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import xyz.artenes.budget.core.Messages
import xyz.artenes.budget.app.presenter.DatePresenter
import xyz.artenes.budget.app.presenter.LabelPresenter
import xyz.artenes.budget.app.presenter.MoneyPresenter
import xyz.artenes.budget.core.serializer.DateSerializer
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class PresenterModule {

    @Singleton
    @Provides
    fun providesDatePresenter(messages: Messages): DatePresenter {
        return DatePresenter(messages)
    }

    @Singleton
    @Provides
    fun providesLabelPresenter(messages: Messages): LabelPresenter {
        return LabelPresenter(messages)
    }

    @Singleton
    @Provides
    fun providesMoneyPresenter(): MoneyPresenter {
        return MoneyPresenter()
    }

    @Singleton
    @Provides
    fun providesDateSerializer(): DateSerializer {
        return DateSerializer()
    }

}