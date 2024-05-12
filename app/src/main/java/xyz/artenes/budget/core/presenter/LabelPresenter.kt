package xyz.artenes.budget.core.presenter

import xyz.artenes.budget.R
import xyz.artenes.budget.app.transaction.search.SearchViewModel
import xyz.artenes.budget.core.Messages
import xyz.artenes.budget.core.models.TransactionType
import xyz.artenes.budget.data.models.CategoryEntity
import xyz.artenes.budget.core.models.SearchSortType
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LabelPresenter @Inject constructor(private val androidMessages: Messages) {

    fun present(type: TransactionType): String {
        return when (type) {
            TransactionType.INCOME -> androidMessages.get(R.string.income)
            TransactionType.EXPENSE -> androidMessages.get(R.string.expense)
        }
    }

    fun present(type: SearchViewModel.DisplayType): String {
        return when (type) {
            SearchViewModel.DisplayType.EXPENSE -> androidMessages.get(R.string.expense)
            SearchViewModel.DisplayType.INCOME -> androidMessages.get(R.string.income)
            SearchViewModel.DisplayType.ALL -> androidMessages.get(R.string.all)
        }
    }

    fun present(category: SearchViewModel.CategoryParam): String {
        return if (category.value == null) {
            androidMessages.get(R.string.all)
        } else {
            category.value.name
        }
    }

    fun present(sort: SearchSortType): String {
        return when (sort) {
            SearchSortType.DATE_ASC -> androidMessages.get(R.string.sort_date_asc)
            SearchSortType.DATE_DESC -> androidMessages.get(R.string.sort_date_desc)
            SearchSortType.NAME_ASC -> androidMessages.get(R.string.sort_name_asc)
            SearchSortType.NAME_DESC -> androidMessages.get(R.string.sort_name_desc)
            SearchSortType.VALUE_ASC -> androidMessages.get(R.string.sort_value_asc)
            SearchSortType.VALUE_DESC -> androidMessages.get(R.string.sort_value_desc)
        }
    }

    fun presentWithDetail(categoryEntity: CategoryEntity): String {
        return "${categoryEntity.name} (${present(categoryEntity.type)})"
    }

}