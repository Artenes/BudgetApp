package xyz.artenes.budget.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import xyz.artenes.budget.core.DateSerializer
import xyz.artenes.budget.core.TransactionType
import xyz.artenes.budget.utils.LocalDateRange
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.CoroutineContext

@Singleton
class AppRepository @Inject constructor(
    private val appDatabase: AppDatabase,
    private val dispatcher: CoroutineContext,
    private val dateSerializer: DateSerializer
) {

    fun getAllTransactions() = appDatabase.transactionsDao().getAll()

    /**
     * This returns all transactions for a given month, grouped by date
     */
    fun getGroupsByMonth(date: LocalDate) =
        appDatabase.transactionsDao()
            .getByMonth("${dateSerializer.serializeYearAndMonth(date)}%")
            .map(this::groupTransactions)

    fun getByMonth(date: LocalDate) = getGroupsByMonth(date).map { groups ->
        groups.flatMap { group ->
            group.transactions
        }
    }

    fun getByDay(date: LocalDate, query: String) =
        appDatabase.transactionsDao()
            .getByDay(date, "%$query%").map(this::groupTransactions)

    fun getByRange(week: LocalDateRange) =
        appDatabase.transactionsDao()
            .getByRange(
                week.startInclusive,
                week.endInclusive
            ).map(this::groupTransactions)

    fun getByYear(year: Int) =
        appDatabase.transactionsDao()
            .getByYear("$year%").map(this::groupTransactions)

    suspend fun saveTransaction(transaction: TransactionEntity) {
        withContext(dispatcher) {
            appDatabase.transactionsDao().insert(transaction)
        }
    }

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