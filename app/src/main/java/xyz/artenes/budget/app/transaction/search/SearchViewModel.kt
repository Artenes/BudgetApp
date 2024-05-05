package xyz.artenes.budget.app.transaction.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import xyz.artenes.budget.app.transaction.list.TransactionItem
import xyz.artenes.budget.core.Money
import xyz.artenes.budget.core.TransactionType
import xyz.artenes.budget.data.AppRepository
import xyz.artenes.budget.data.SearchResultsData
import xyz.artenes.budget.data.TransactionWithCategoryEntity
import xyz.artenes.budget.utils.DataState
import xyz.artenes.budget.utils.DatePresenter
import xyz.artenes.budget.utils.LabelPresenter
import xyz.artenes.budget.utils.LocalDateRange
import xyz.artenes.budget.utils.ValueAndLabel
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    database: AppRepository,
    private val datePresenter: DatePresenter,
    private val labelPresenter: LabelPresenter
) : ViewModel() {

    private val _dateFilter = MutableStateFlow(
        DateFilterItem(
            datePresenter.formatMonthAndYear(LocalDateRange.now().startInclusive),
            DateFilter(DateFilterType.MONTH, LocalDateRange.now())
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

    val transactions = database.getByMonth(LocalDate.now())
        .map { items ->

            val transactions = items.map(this::transactionToItem)
            val totalIncome = Money(transactions.filter { it.type == TransactionType.INCOME }
                .sumOf { it.amount.value })
            val totalExpense = Money(transactions.filter { it.type == TransactionType.EXPENSE }
                .sumOf { it.amount.value })
            val balance = totalIncome.minus(totalExpense)
            val count = transactions.size

            DataState.Success(
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
                    totalTransactions = count,
                    transactions = transactions
                )
            )

        }
        .stateIn(viewModelScope, SharingStarted.Lazily, DataState.Loading)

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
            type = transaction.type
        )
    }

    enum class DisplayType {
        EXPENSE,
        INCOME,
        ALL
    }

}