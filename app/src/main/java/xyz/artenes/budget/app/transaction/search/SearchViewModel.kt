package xyz.artenes.budget.app.transaction.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import xyz.artenes.budget.app.transaction.list.TransactionItem
import xyz.artenes.budget.core.Money
import xyz.artenes.budget.core.TransactionType
import xyz.artenes.budget.data.AppRepository
import xyz.artenes.budget.data.SearchResultsData
import xyz.artenes.budget.data.TransactionWithCategoryEntity
import xyz.artenes.budget.utils.DataState
import xyz.artenes.budget.utils.LocaleFormatter
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    database: AppRepository,
    private val formatter: LocaleFormatter
) : ViewModel() {

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
                    formattedTotalExpenses = formatter.formatMoney(totalExpense),
                    totalIncome = totalIncome,
                    formattedTotalIncome = formatter.formatMoney(totalIncome),
                    balance = balance,
                    formattedBalance = formatter.formatMoney(balance),
                    totalTransactions = count,
                    transactions = transactions
                )
            )

        }
        .stateIn(viewModelScope, SharingStarted.Lazily, DataState.Loading)

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

        val currencySymbol = formatter.getCurrencySymbol()
        val formattedValue = formatter.formatMoney(transaction.amount)

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

}