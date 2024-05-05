package xyz.artenes.budget.utils

import xyz.artenes.budget.R
import xyz.artenes.budget.android.Messages
import xyz.artenes.budget.app.transaction.search.SearchViewModel
import xyz.artenes.budget.core.TransactionType
import xyz.artenes.budget.data.CategoryEntity
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

    fun present(sort: SearchViewModel.SearchSortType): String {
        return when(sort) {
            SearchViewModel.SearchSortType.DATE_ASC -> messages.get(R.string.sort_date_asc)
            SearchViewModel.SearchSortType.DATE_DESC -> messages.get(R.string.sort_date_desc)
            SearchViewModel.SearchSortType.NAME_ASC -> messages.get(R.string.sort_name_asc)
            SearchViewModel.SearchSortType.NAME_DESC -> messages.get(R.string.sort_name_desc)
            SearchViewModel.SearchSortType.VALUE_ASC -> messages.get(R.string.sort_value_asc)
            SearchViewModel.SearchSortType.VALUE_DESC -> messages.get(R.string.sort_value_desc)
        }
    }

    fun presentWithDetail(categoryEntity: CategoryEntity): String {
        return "${categoryEntity.name} (${present(categoryEntity.type)})"
    }

}