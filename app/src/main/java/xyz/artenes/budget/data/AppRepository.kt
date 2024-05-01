package xyz.artenes.budget.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import xyz.artenes.budget.core.TransactionType
import xyz.artenes.budget.utils.DateRangeInclusive
import xyz.artenes.budget.utils.Year
import xyz.artenes.budget.utils.YearMonth
import xyz.artenes.budget.utils.YearMonthDay
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.CoroutineContext

@Singleton
class AppRepository @Inject constructor(
    private val appDatabase: AppDatabase,
    private val dispatcher: CoroutineContext
) {

    fun getAllTransactions() = appDatabase.transactionsDao().getAll()

    /**
     * This returns all transactions for a given month, grouped by date
     */
    fun getByMonth(yearMonth: YearMonth) =
        appDatabase.transactionsDao()
            .getByMonth("${yearMonth}%").map(this::groupTransactions)


    fun getByDay(yearMonthDay: YearMonthDay) =
        appDatabase.transactionsDao()
            .getByDay(yearMonthDay.toLocalDate()).map(this::groupTransactions)

    fun getByWeek(week: DateRangeInclusive) =
        appDatabase.transactionsDao()
            .getByWeek(
                YearMonthDay.fromLocalDate(week.start).toString(),
                YearMonthDay.fromLocalDate(week.end).toString()
            ).map(this::groupTransactions)

    fun getByYear(year: Year) =
        appDatabase.transactionsDao()
            .getByYear("$year%").map(this::groupTransactions)

    suspend fun saveTransaction(transaction: TransactionEntity) {
        withContext(dispatcher) {
            appDatabase.transactionsDao().insert(transaction)
        }
    }

    fun totalAmountForMonthAsFlow(yearMonth: YearMonth): Flow<Int> =
        appDatabase.transactionsDao().totalAmountForMonthAsFlow("$yearMonth%")

    suspend fun getCategoriesByType(type: TransactionType): List<CategoryEntity> {
        return withContext(dispatcher) {
            appDatabase.categoryDao().getAllNotDeletedByType(type)
        }
    }

    private fun groupTransactions(transactions: List<TransactionWithCategoryEntity>) =
        transactions.groupBy { transaction -> transaction.date }
            .entries.map { entry ->
                //then for each group, sort the list of transactions by created time
                //so groups are sorted by date and transactions are sorted by created time
                val sortedList = entry.value.sortedByDescending { it.createdAt }
                TransactionGroup(sortedList, entry.key)
            }

}