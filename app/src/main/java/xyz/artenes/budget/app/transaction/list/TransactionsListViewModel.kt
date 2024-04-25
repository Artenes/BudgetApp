package xyz.artenes.budget.app.transaction.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import xyz.artenes.budget.core.Money
import xyz.artenes.budget.data.AppRepository
import xyz.artenes.budget.utils.YearAndMonth
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class TransactionsListViewModel @Inject constructor(private val repository: AppRepository) :
    ViewModel() {

    val transactions =
        repository.getAllTransactionsWithCategoryByMonthGroupedByDate(
            YearAndMonth.fromLocalDate(
                LocalDate.now()
            )
        ).stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

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
        //then we return the value as money
        Money(total)
    }.stateIn(viewModelScope, SharingStarted.Lazily, Money(0))

}