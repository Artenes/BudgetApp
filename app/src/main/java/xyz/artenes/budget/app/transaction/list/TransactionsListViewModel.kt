package xyz.artenes.budget.app.transaction.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import xyz.artenes.budget.R
import xyz.artenes.budget.android.Messages
import xyz.artenes.budget.android.UserPreferences
import xyz.artenes.budget.core.Money
import xyz.artenes.budget.core.TransactionType
import xyz.artenes.budget.data.AppRepository
import xyz.artenes.budget.data.TransactionGroup
import xyz.artenes.budget.data.TransactionWithCategoryEntity
import xyz.artenes.budget.utils.LoadingData
import xyz.artenes.budget.utils.LocaleFormatter
import xyz.artenes.budget.utils.Year
import xyz.artenes.budget.utils.YearMonth
import xyz.artenes.budget.utils.YearMonthDay
import java.time.LocalDate
import javax.inject.Inject

/*

sealed class DataState<out T> {
    data class Success<out T>(val data: T) : DataState<T>()
    object Loading : DataState<Nothing>()
    data class Error(val exception: Exception) : DataState<Nothing>()
}

val myData: Flow<DataState<List<MyData>>> = myDao.getAll()
        .map { DataState.Success(it) } // map data to Success state
        .catch { e -> emit(DataState.Error(e)) } // catch any error and emit Error state
        .onStart { emit(DataState.Loading) } // emit Loading state at the start

val myData: Flow<DataState<List<MyData>>> = flow {
        emit(DataState.Loading)
        try {
            val data = myDao.getAll()
            emit(DataState.Success(data))
        } catch (e: Exception) {
            emit(DataState.Error(e))
        }
    }
}

 */

@HiltViewModel
class TransactionsListViewModel @Inject constructor(
    repository: AppRepository,
    private val formatter: LocaleFormatter,
    private val messages: Messages,
    private val preferences: UserPreferences
) :
    ViewModel() {

    /*
    Date Filter
     */
    val filters = preferences.listen("filter", DateFilterType.MONTH.toString()).map {

        val selected = enumValueOf<DateFilterType>(it)

        listOf(
            DateFilterItem(
                DateFilterType.DAY,
                messages.get(R.string.day),
                selected == DateFilterType.DAY
            ),
            DateFilterItem(
                DateFilterType.WEEK,
                messages.get(R.string.week),
                selected == DateFilterType.WEEK
            ),
            DateFilterItem(
                DateFilterType.MONTH,
                messages.get(R.string.month),
                selected == DateFilterType.MONTH
            ),
            DateFilterItem(
                DateFilterType.YEAR,
                messages.get(R.string.year),
                selected == DateFilterType.YEAR
            ),
            DateFilterItem(
                DateFilterType.CUSTOM,
                messages.get(R.string.custom_range),
                selected == DateFilterType.CUSTOM
            ),
        )

    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    /*
    Date filter value
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    val filterValue = preferences.listen("filter", DateFilterType.MONTH.toString()).flatMapLatest {
        val filter = enumValueOf<DateFilterType>(it)

        val now = LocalDate.now()
        val defaultValue = when (filter) {
            DateFilterType.DAY -> YearMonthDay.fromLocalDate(now).toString()
            DateFilterType.WEEK -> "2024-04-28 ~ 2024-04-28"
            DateFilterType.MONTH -> YearMonth.fromLocalDate(now).toString()
            DateFilterType.YEAR -> Year.fromLocalDate(now).toString()
            DateFilterType.CUSTOM -> "2024-04-28 ~ 2024-04-28"
        }

        preferences.listen("${it}_VALUE", defaultValue)
    }.map { value ->

        val filter =
            enumValueOf<DateFilterType>(preferences.get("filter", DateFilterType.MONTH.toString()))

        val label = when (filter) {
            DateFilterType.DAY -> {
                formatter.formatDate(YearMonthDay.fromString(value).toLocalDate())
            }

            DateFilterType.WEEK -> {
                "2024-04-28 ~ 2024-04-28"
            }

            DateFilterType.MONTH -> {
                formatter.formatMonthAndYear(YearMonth.fromString(value).toLocalDate())
            }

            DateFilterType.YEAR -> {
                formatter.formatYear(Year.fromString(value).toLocalDate())
            }

            DateFilterType.CUSTOM -> {
                "2024-04-28 ~ 2024-04-28"
            }
        }

        DateFilterValueItem(filter, value, label)

    }.stateIn(
        viewModelScope,
        SharingStarted.Lazily,
        DateFilterValueItem(
            DateFilterType.MONTH,
            YearMonth.now().toString(),
            formatter.formatMonthAndYear(YearMonth.now().toLocalDate())
        )
    )

    /**
     * List of transactions to display
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    val transactions = filterValue.flatMapLatest {

        when (it.type) {
            DateFilterType.DAY -> {
                repository.getByDay(YearMonthDay.fromString(it.value))
            }

            DateFilterType.WEEK -> {
                repository.getByMonth(YearMonth.now())
            }

            DateFilterType.MONTH -> {
                repository.getByMonth(YearMonth.fromString(it.value))
            }

            DateFilterType.YEAR -> {
                repository.getByYear(Year.fromString(it.value))
            }

            DateFilterType.CUSTOM -> {
                repository.getByMonth(YearMonth.now())
            }
        }

    }.map { groups ->
        //then format them for display
        groups.map { group ->
            groupToItem(group)
        }
    }.map {
        //then inform that loading is complete
        LoadingData(false, it)
    }.stateIn(viewModelScope, SharingStarted.Lazily, LoadingData(true, null))

    /**
     * This sum all amounts from the transactions returned from flow above
     * and reduce them to an integer
     */
    val incomeTotal = transactions.map { loadingData ->
        val total = loadingData.data?.sumOf { group ->
            group.transactions.filter { it.type == TransactionType.INCOME }.sumOf { transaction ->
                transaction.amount.value
            }
        } ?: 0
        //then we return the value as a formatted string
        val currencySymbol = formatter.getCurrencySymbol()
        val formattedValue = formatter.formatMoney(Money(total))
        "$currencySymbol $formattedValue"
    }.stateIn(viewModelScope, SharingStarted.Lazily, "")

    val expenseTotal = transactions.map { loadingData ->
        val total = loadingData.data?.sumOf { group ->
            group.transactions.filter { it.type == TransactionType.EXPENSE }.sumOf { transaction ->
                transaction.amount.value
            }
        } ?: 0
        //then we return the value as a formatted string
        val currencySymbol = formatter.getCurrencySymbol()
        val formattedValue = formatter.formatMoney(Money(total))
        "$currencySymbol $formattedValue"
    }.stateIn(viewModelScope, SharingStarted.Lazily, "")

    val amountOfTransactions = transactions.map { loadingData ->

        loadingData.data?.sumOf { group ->
            group.transactions.size
        } ?: 0

    }.stateIn(viewModelScope, SharingStarted.Lazily, 0)

    /**
     * Set the current filter
     */
    fun setFilter(item: DateFilterItem) {
        viewModelScope.launch {
            preferences.save("filter", item.type.toString())
        }
    }

    fun setFilterValue(item: DateFilterValueItem) {
        viewModelScope.launch {
            preferences.save(item.key, item.value)
        }
    }

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