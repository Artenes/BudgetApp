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
import xyz.artenes.budget.core.DateSerializer
import xyz.artenes.budget.core.Money
import xyz.artenes.budget.core.TransactionType
import xyz.artenes.budget.data.AppRepository
import xyz.artenes.budget.data.TransactionGroup
import xyz.artenes.budget.data.TransactionWithCategoryEntity
import xyz.artenes.budget.utils.DateRangeInclusive
import xyz.artenes.budget.utils.LoadingData
import xyz.artenes.budget.utils.LocaleFormatter
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
    private val preferences: UserPreferences,
    private val dateSerializer: DateSerializer
) :
    ViewModel() {

    /*
    Date Filter
    @TODO date filter and its value should be MutableStates inside viewmodel instead of persisted in storage
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
            DateFilterType.DAY -> dateSerializer.serializeDate(now)
            DateFilterType.WEEK -> DateRangeInclusive.now().toString()
            DateFilterType.MONTH -> dateSerializer.serializeYearAndMonth(now)
            DateFilterType.YEAR -> now.year.toString()
            DateFilterType.CUSTOM -> "2024-04-28 ~ 2024-04-28"
        }

        preferences.listen("${it}_VALUE", defaultValue)
    }.map { value ->

        val filter =
            enumValueOf<DateFilterType>(preferences.get("filter", DateFilterType.MONTH.toString()))

        val label = when (filter) {
            DateFilterType.DAY -> {
                formatter.formatDate(LocalDate.parse(value))
            }

            DateFilterType.WEEK -> {
                formatter.formatRange(DateRangeInclusive.fromString(value))
            }

            DateFilterType.MONTH -> {
                formatter.formatMonthAndYear(dateSerializer.deserializeYearAndMonth(value))
            }

            DateFilterType.YEAR -> {
                value
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
            dateSerializer.serializeYearAndMonth(LocalDate.now()),
            formatter.formatMonthAndYear(LocalDate.now())
        )
    )

    /**
     * List of transactions to display
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    val transactions = filterValue.flatMapLatest {

        when (it.type) {
            DateFilterType.DAY -> {
                repository.getByDay(LocalDate.parse(it.value))
            }

            DateFilterType.WEEK -> {
                repository.getByWeek(DateRangeInclusive.fromString(it.value))
            }

            DateFilterType.MONTH -> {
                val date = dateSerializer.deserializeYearAndMonth(it.value)
                repository.getByMonth(date)
            }

            DateFilterType.YEAR -> {
                repository.getByYear(it.value.toInt())
            }

            DateFilterType.CUSTOM -> {
                val date = dateSerializer.deserializeYearAndMonth(it.value)
                repository.getByMonth(date)
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

    fun setValueForDay(item: DateFilterValueItem, newValue: LocalDate) {
        viewModelScope.launch {
            preferences.save(item.key, dateSerializer.serializeDate(newValue))
        }
    }

    fun setValueForWeek(item: DateFilterValueItem, newValue: DateRangeInclusive) {
        viewModelScope.launch {
            preferences.save(item.key, newValue.toString())
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