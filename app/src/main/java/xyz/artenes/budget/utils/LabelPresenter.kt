package xyz.artenes.budget.utils

import xyz.artenes.budget.R
import xyz.artenes.budget.android.Messages
import xyz.artenes.budget.app.transaction.search.SearchViewModel
import xyz.artenes.budget.core.TransactionType
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LabelPresenter @Inject constructor(private val messages: Messages) {

    fun present(type: TransactionType): String {
        return when (type) {
            TransactionType.INCOME -> messages.get(R.string.income)
            TransactionType.EXPENSE -> messages.get(R.string.expense)
        }
    }

    fun present(type: SearchViewModel.DisplayType): String {
        return when (type) {
            SearchViewModel.DisplayType.EXPENSE -> messages.get(R.string.expense)
            SearchViewModel.DisplayType.INCOME -> messages.get(R.string.income)
            SearchViewModel.DisplayType.ALL -> messages.get(R.string.all)
        }
    }

}