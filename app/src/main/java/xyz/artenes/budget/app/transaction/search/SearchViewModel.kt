package xyz.artenes.budget.app.transaction.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.launch
import xyz.artenes.budget.app.transaction.list.TransactionItem
import xyz.artenes.budget.core.Money
import xyz.artenes.budget.core.TransactionType
import xyz.artenes.budget.data.AppRepository
import xyz.artenes.budget.data.CategoryEntity
import xyz.artenes.budget.data.SearchResultsData
import xyz.artenes.budget.data.SearchSortType
import xyz.artenes.budget.data.TransactionWithCategoryEntity
import xyz.artenes.budget.utils.DataState
import xyz.artenes.budget.utils.DatePresenter
import xyz.artenes.budget.utils.LabelPresenter
import xyz.artenes.budget.utils.LocalDateRange
import xyz.artenes.budget.utils.ValueAndLabel
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val repository: AppRepository,
    private val datePresenter: DatePresenter,
    private val labelPresenter: LabelPresenter
) : ViewModel() {

    private val _query = MutableStateFlow("")
    val query : StateFlow<String> = _query

    private val _dateFilter = MutableStateFlow(
        DateFilterItem(
            datePresenter.formatMonthAndYear(LocalDateRange.today().startInclusive),
            DateFilter(DateFilterType.MONTH, LocalDateRange.currentMonth())
        )
    )
    val dateFilter: StateFlow<DateFilterItem> = _dateFilter

    private val _types = MutableStateFlow(
        listOf(
            ValueAndLabel(DisplayType.ALL, labelPresenter.present(DisplayType.ALL), true),
            ValueAndLabel(DisplayType.EXPENSE, labelPresenter.present(DisplayType.EXPENSE), false),
            ValueAndLabel(DisplayType.INCOME, labelPresenter.present(DisplayType.INCOME), false)
        )
    )
    val types: StateFlow<List<ValueAndLabel<DisplayType>>> = _types

    private val _sorts = MutableStateFlow(
        listOf(
            ValueAndLabel(
                SearchSortType.DATE_DESC,
                labelPresenter.present(SearchSortType.DATE_DESC),
                true
            ),
            ValueAndLabel(
                SearchSortType.DATE_ASC,
                labelPresenter.present(SearchSortType.DATE_ASC),
                false
            ),
            ValueAndLabel(
                SearchSortType.VALUE_ASC,
                labelPresenter.present(SearchSortType.VALUE_ASC),
                false
            ),
            ValueAndLabel(
                SearchSortType.VALUE_DESC,
                labelPresenter.present(SearchSortType.VALUE_DESC),
                false
            ),
            ValueAndLabel(
                SearchSortType.NAME_ASC,
                labelPresenter.present(SearchSortType.NAME_ASC),
                false
            ),
            ValueAndLabel(
                SearchSortType.NAME_DESC,
                labelPresenter.present(SearchSortType.NAME_DESC),
                false
            ),
        )
    )
    val sorts: StateFlow<List<ValueAndLabel<SearchSortType>>> = _sorts

    private val _categories = MutableStateFlow<List<ValueAndLabel<CategoryParam>>>(emptyList())
    val categories: StateFlow<List<ValueAndLabel<CategoryParam>>> = _categories

    private val _transactions = MutableStateFlow<DataState<SearchResultsData>>(DataState.Loading)
    val transactions: StateFlow<DataState<SearchResultsData>> = _transactions

    init {
        viewModelScope.launch {
            listenForDateChanges()
            listenForTypeChange()
            listenForCategoryChange()
            listenForSortChanges()
            loadCategories()
            loadTransactions()
        }
    }

    private fun listenForTypeChange() {
        viewModelScope.launch {
            _types.drop(1).collectLatest {
                loadCategories()
                loadTransactions()
            }
        }
    }

    private fun listenForCategoryChange() {
        viewModelScope.launch {
            _categories.drop(1).collectLatest {
                loadTransactions()
            }
        }
    }

    private fun listenForDateChanges() {
        viewModelScope.launch {
            _dateFilter.drop(1).collectLatest {
                loadTransactions()
            }
        }
    }

    private fun listenForSortChanges() {
        viewModelScope.launch {
            _sorts.drop(1).collectLatest {
                loadTransactions()
            }
        }
    }

    fun setDateFilter(filter: DateFilter) {
        val label = when (filter.type) {
            DateFilterType.DAY -> datePresenter.formatDate(filter.value.startInclusive)
            DateFilterType.WEEK -> datePresenter.formatRange(filter.value)
            DateFilterType.MONTH -> datePresenter.formatMonthAndYear(filter.value.startInclusive)
            DateFilterType.YEAR -> filter.value.startInclusive.year.toString()
            DateFilterType.CUSTOM -> datePresenter.formatRange(filter.value)
        }
        _dateFilter.value = DateFilterItem(label, filter)
    }

    fun setType(type: ValueAndLabel<DisplayType>) {
        _types.value = _types.value.map { it.copy(selected = it == type) }
    }

    fun setCategory(category: ValueAndLabel<CategoryParam>) {
        _categories.value = _categories.value.map { it.copy(selected = it == category) }
    }

    fun setSort(sort: ValueAndLabel<SearchSortType>) {
        _sorts.value = _sorts.value.map { it.copy(selected = it == sort) }
    }

    fun search(query: String) {
        viewModelScope.launch {
            _query.value = query
            loadTransactions()
        }
    }

    private suspend fun loadCategories() {
        val types = _types.value
        val selectedType = types.first { it.selected }

        val categories = if (selectedType.value == DisplayType.ALL) {
            repository.getAllCategories()
        } else {
            val transactionType = when (selectedType.value) {
                DisplayType.EXPENSE -> TransactionType.EXPENSE
                DisplayType.INCOME -> TransactionType.INCOME
                else -> throw RuntimeException("Can't parse DisplayType to TransactionType")
            }
            repository.getCategoriesByType(transactionType)
        }

        val items = categories.map { category ->

            val label = if (selectedType.value == DisplayType.ALL) {
                labelPresenter.presentWithDetail(category)
            } else {
                category.name
            }

            ValueAndLabel(CategoryParam(category), label, false)
        }.toMutableList()

        val allCategory = CategoryParam()
        items.add(0, ValueAndLabel(allCategory, labelPresenter.present(allCategory), true))

        _categories.value = items
    }

    private suspend fun loadTransactions() {

        val date = _dateFilter.value.value.value
        val type = _types.value.first { it.selected }.value
        val category = _categories.value.firstOrNull { it.selected }?.value?.value
        val sort = _sorts.value.first { it.selected }.value
        val query = _query.value

        val items = repository.search(
            range = date,
            sort = sort,
            description = query,
            category = category,
            type = when (type) {
                DisplayType.ALL -> null
                DisplayType.INCOME -> TransactionType.INCOME
                DisplayType.EXPENSE -> TransactionType.EXPENSE
            }
        )
        val transactions = items.map(this::transactionToItem)
        val totalIncome = Money(transactions.filter { it.type == TransactionType.INCOME }
            .sumOf { it.amount.value })
        val totalExpense = Money(transactions.filter { it.type == TransactionType.EXPENSE }
            .sumOf { it.amount.value })
        val balance = totalIncome.minus(totalExpense)
        val count = transactions.size

        _transactions.value = DataState.Success(
            SearchResultsData(
                totalExpenses = totalExpense,
                formattedTotalExpenses = "- ${datePresenter.formatMoneyWithCurrency(totalExpense)}",
                totalIncome = totalIncome,
                formattedTotalIncome = "+ ${datePresenter.formatMoneyWithCurrency(totalIncome)}",
                balance = balance,
                formattedBalance = "${if (balance.value >= 0) "+" else "-"} ${
                    datePresenter.formatMoneyWithCurrency(
                        balance.absolute()
                    )
                }",
                balanceOpacity = if (balance.value >= 0) 1.0f else 0.8f,
                totalTransactions = count,
                transactions = transactions
            )
        )


    }

    /**
     * Format transactions to be displayed by the UI
     */
    private fun transactionToItem(transaction: TransactionWithCategoryEntity): TransactionItem {
        val sign = if (transaction.type == TransactionType.EXPENSE) {
            "-"
        } else {
            "+"
        }

        val colorAlpha = if (transaction.type == TransactionType.EXPENSE) {
            0.7f
        } else {
            1.0f
        }

        val currencySymbol = datePresenter.getCurrencySymbol()
        val formattedValue = datePresenter.formatMoney(transaction.amount)

        return TransactionItem(
            id = transaction.id,
            icon = transaction.icon,
            description = transaction.description,
            colorAlpha = colorAlpha,
            formattedAmount = "$sign $currencySymbol $formattedValue",
            amount = transaction.amount,
            type = transaction.type,
            date = transaction.date,
            formattedDate = datePresenter.formatDate(transaction.date)
        )
    }

    enum class DisplayType {
        EXPENSE,
        INCOME,
        ALL
    }

    data class CategoryParam(val value: CategoryEntity? = null)

}