package xyz.artenes.budget.app.transaction.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import xyz.artenes.budget.BuildConfig
import xyz.artenes.budget.R
import xyz.artenes.budget.android.Messages
import xyz.artenes.budget.core.Money
import xyz.artenes.budget.core.TransactionType
import xyz.artenes.budget.data.AppRepository
import xyz.artenes.budget.data.DatabaseSeeder
import xyz.artenes.budget.data.TransactionGroup
import xyz.artenes.budget.data.TransactionWithCategoryEntity
import xyz.artenes.budget.data.TransactionsData
import xyz.artenes.budget.utils.DataState
import xyz.artenes.budget.utils.LocalDateRange
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

//TODO date filter and its value should be MutableStates inside viewmodel instead of persisted in storage
@HiltViewModel
class TransactionsListViewModel @Inject constructor(
    private val seeder: DatabaseSeeder,
    private val repository: AppRepository,
    private val formatter: LocaleFormatter,
    private val messages: Messages
) :
    ViewModel() {

    private val filterValuesMap = loadFilterValueMap()

    private val _loading = MutableStateFlow(true)
    val loading: StateFlow<Boolean> = _loading

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query

    private val _filters =
        MutableStateFlow<DataState<List<DateFilterItem>>>(DataState.Uninitialized)
    val filters: StateFlow<DataState<List<DateFilterItem>>> = _filters

    private val _filterValue =
        MutableStateFlow<DataState<DateFilterValueItem>>(DataState.Uninitialized)
    val filterValue: StateFlow<DataState<DateFilterValueItem>> = _filterValue

    private val _transactionsData =
        MutableStateFlow<DataState<TransactionsData>>(DataState.Uninitialized)
    val transactionsData: StateFlow<DataState<TransactionsData>> = _transactionsData

    init {
        viewModelScope.launch {
            seedDatabase()
            loadFiltersType()
            loadFilterValue()
            loadTransactionsData()
            _loading.value = false
        }
    }

    private fun loadFilterValueMap(): MutableMap<DateFilterType, Any> {
        val now = LocalDate.now()
        val interval = LocalDateRange.now()
        return mutableMapOf(
            DateFilterType.DAY to now,
            DateFilterType.WEEK to interval,
            DateFilterType.MONTH to now,
            DateFilterType.YEAR to now,
            DateFilterType.CUSTOM to interval
        )
    }

    private suspend fun seedDatabase() {
        if (BuildConfig.LOAD_DATA) {
            seeder.seedTest()
        } else {
            seeder.seed()
        }
    }

    private fun loadFiltersType() {
        _filters.value = DataState.Success(
            listOf(
                DateFilterItem(
                    DateFilterType.DAY,
                    messages.get(R.string.day),
                    false
                ),
                DateFilterItem(
                    DateFilterType.WEEK,
                    messages.get(R.string.week),
                    false
                ),
                DateFilterItem(
                    DateFilterType.MONTH,
                    messages.get(R.string.month),
                    true
                ),
                DateFilterItem(
                    DateFilterType.YEAR,
                    messages.get(R.string.year),
                    false
                ),
                DateFilterItem(
                    DateFilterType.CUSTOM,
                    messages.get(R.string.custom_range),
                    false
                )
            )
        )
    }

    private fun loadFilterValue() {
        if (_filters.value !is DataState.Success) {
            return
        }

        val type = (_filters.value as DataState.Success).data.first { it.selected }.type
        val value = filterValuesMap[type]!!
        val label = when (type) {
            DateFilterType.DAY -> formatter.formatDate(value as LocalDate)
            DateFilterType.WEEK -> formatter.formatRange(value as LocalDateRange)
            DateFilterType.MONTH -> formatter.formatMonthAndYear(value as LocalDate)
            DateFilterType.YEAR -> (value as LocalDate).year.toString()
            DateFilterType.CUSTOM -> formatter.formatRange(value as LocalDateRange)
        }

        _filterValue.value = DataState.Success(DateFilterValueItem(type, label, value))
    }

    private suspend fun loadTransactionsData() {

        val query = _query.value
        val filter = (_filterValue.value as DataState.Success).data.type
        val filterValue = filterValuesMap[filter]

        val transactionGroups = when (filter) {
            DateFilterType.DAY -> repository.getByDay(filterValue as LocalDate, query).first()
            DateFilterType.WEEK -> repository.getByRange(filterValue as LocalDateRange).first()
            DateFilterType.MONTH -> repository.getByMonth(filterValue as LocalDate).first()
            DateFilterType.YEAR -> repository.getByYear((filterValue as LocalDate).year).first()
            DateFilterType.CUSTOM -> repository.getByRange(filterValue as LocalDateRange).first()
        }

        val transactions = transactionGroups.flatMap { it.transactions }
        val totalIncome =
            Money(transactions.filter { it.type == TransactionType.INCOME }
                .sumOf { it.amount.value })
        val totalExpense =
            Money(transactions.filter { it.type == TransactionType.EXPENSE }
                .sumOf { it.amount.value })
        val balance = totalIncome.minus(totalExpense)
        val totalTransactions = transactions.size

        _transactionsData.value = DataState.Success(
            TransactionsData(
                totalExpenses = totalExpense,
                formattedTotalExpenses = formatter.formatMoney(totalExpense),
                totalIncome = totalIncome,
                formattedTotalIncome = formatter.formatMoney(totalIncome),
                balance = balance,
                formattedBalance = formatter.formatMoney(balance),
                totalTransactions = totalTransactions,
                groups = transactionGroups.map(this::groupToItem)
            )
        )

    }

    fun search(query: String) {
        viewModelScope.launch {
            _query.value = query
            loadTransactionsData()
        }
    }

    fun setFilterType(item: DateFilterItem) {
        viewModelScope.launch {
            val list = (_filters.value as DataState.Success).data.toMutableList()
            val updatedList = list.map { it.copy(selected = it == item) }
            _filters.value = DataState.Success(updatedList)
            loadFilterValue()
            loadTransactionsData()
        }
    }

    fun setValueForDay(newValue: LocalDate) {
        viewModelScope.launch {
            filterValuesMap[DateFilterType.DAY] = newValue
            _filterValue.value = DataState.Success(
                DateFilterValueItem(
                    DateFilterType.DAY,
                    formatter.formatDate(newValue),
                    newValue
                )
            )
            loadTransactionsData()
        }
    }

    fun setValueForWeek(newValue: LocalDateRange) {
        viewModelScope.launch {
            filterValuesMap[DateFilterType.WEEK] = newValue
            _filterValue.value = DataState.Success(
                DateFilterValueItem(
                    DateFilterType.WEEK,
                    formatter.formatRange(newValue),
                    newValue
                )
            )
            loadTransactionsData()
        }
    }

    /**
     * Format groups to be displayed by the UI
     */
    private fun groupToItem(group: TransactionGroup): TransactionGroupItem {
        return TransactionGroupItem(
            key = group.date,
            date = formatter.formatDateAsRelative(group.date),
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