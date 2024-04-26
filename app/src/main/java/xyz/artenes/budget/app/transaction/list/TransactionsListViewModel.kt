package xyz.artenes.budget.app.transaction.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import xyz.artenes.budget.core.Money
import xyz.artenes.budget.core.TransactionType
import xyz.artenes.budget.data.AppRepository
import xyz.artenes.budget.data.TransactionGroup
import xyz.artenes.budget.data.TransactionWithCategoryEntity
import xyz.artenes.budget.utils.LocaleFormatter
import xyz.artenes.budget.utils.YearAndMonth
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class TransactionsListViewModel @Inject constructor(
    repository: AppRepository,
    private val formatter: LocaleFormatter
) :
    ViewModel() {

    /**
     * List of transactions to display
     */
    val transactions =
        repository.getAllTransactionsWithCategoryByMonthGroupedByDate(
            YearAndMonth.fromLocalDate(
                LocalDate.now()
            )
        ).map { groups ->
            groups.map { group ->
                groupToItem(group)
            }
        }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    /**
     * This sum all amounts from the transactions returned from flow above
     * and reduce them to an integer
     */
    val total = transactions.map { groups ->
        //we have group of transactions, so we sum them first
        val total = groups.sumOf { group ->
            //then for each group, we sum the value from each transaction
            group.transactions.sumOf { transaction ->
                //based on the type of transaction we either reduce or increase the value of the sum
                transaction.amount.toSigned(transaction.type)
            }
        }
        //then we return the value as a formatted string
        val currencySymbol = formatter.getCurrencySymbol()
        val formattedValue = formatter.formatMoney(Money(total))
        "$currencySymbol $formattedValue"
    }.stateIn(viewModelScope, SharingStarted.Lazily, "")

    /**
     * Format groups to be displayed by the UI
     */
    private fun groupToItem(group: TransactionGroup): TransactionGroupItem {
        return TransactionGroupItem(
            key = group.date,
            date = formatter.formatDateAsRelative(group.date),
            transactions = group.transactions.map { transaction -> transactionToItem(transaction) }
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