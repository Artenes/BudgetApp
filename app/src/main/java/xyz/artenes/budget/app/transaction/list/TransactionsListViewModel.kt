package xyz.artenes.budget.app.transaction.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import xyz.artenes.budget.BuildConfig
import xyz.artenes.budget.android.Messages
import xyz.artenes.budget.app.presenters.MoneyPresenter
import xyz.artenes.budget.core.Money
import xyz.artenes.budget.core.TransactionType
import xyz.artenes.budget.data.AppRepository
import xyz.artenes.budget.data.DatabaseSeeder
import xyz.artenes.budget.data.TransactionGroup
import xyz.artenes.budget.data.TransactionWithCategoryEntity
import xyz.artenes.budget.data.TransactionsData
import xyz.artenes.budget.utils.DataState
import xyz.artenes.budget.utils.DatePresenter
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class TransactionsListViewModel @Inject constructor(
    private val seeder: DatabaseSeeder,
    private val repository: AppRepository,
    private val datePresenter: DatePresenter,
    private val moneyPresenter: MoneyPresenter,
    private val messages: Messages
) :
    ViewModel() {

    val transactionsData: StateFlow<DataState<TransactionsData>> =
        flow {

            emit(DataState.Loading)

            if (BuildConfig.LOAD_DATA) {
                seeder.seedTest()
            } else {
                seeder.seed()
            }

            val now = LocalDate.now()
            emitAll(repository.getGroupsByMonth(now).map { groups ->

                val transactions = groups.flatMap { it.transactions }
                val totalIncome =
                    Money(transactions.filter { it.type == TransactionType.INCOME }
                        .sumOf { it.amount.value })
                val totalExpense =
                    Money(transactions.filter { it.type == TransactionType.EXPENSE }
                        .sumOf { it.amount.value })
                val balance = totalIncome.minus(totalExpense)
                val totalTransactions = transactions.size

                DataState.Success(
                    TransactionsData(
                        totalExpenses = totalExpense,
                        formattedTotalExpenses = moneyPresenter.formatMoneyWithCurrency(totalExpense),
                        totalIncome = totalIncome,
                        formattedTotalIncome = moneyPresenter.formatMoney(totalIncome),
                        balance = balance,
                        formattedBalance = moneyPresenter.formatMoney(balance),
                        totalTransactions = totalTransactions,
                        groups = groups.map { item -> groupToItem(item) },
                        formattedCurrentMonth = datePresenter.formatMonth(now)
                    )
                )

            })

        }.stateIn(viewModelScope, SharingStarted.Lazily, DataState.Loading)

    /**
     * Format groups to be displayed by the UI
     */
    private fun groupToItem(group: TransactionGroup): TransactionGroupItem {
        return TransactionGroupItem(
            key = group.date,
            date = datePresenter.formatDateAsRelative(group.date),
            transactions = group.transactions.map(this::transactionToItem)
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

        val currencySymbol = moneyPresenter.getCurrencySymbol()
        val formattedValue = moneyPresenter.formatMoney(transaction.amount)

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

}