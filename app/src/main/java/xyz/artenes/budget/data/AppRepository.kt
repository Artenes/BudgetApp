package xyz.artenes.budget.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import xyz.artenes.budget.core.TransactionType
import xyz.artenes.budget.utils.YearAndMonth
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
    fun getAllTransactionsWithCategoryByMonthGroupedByDate(yearAndMonth: YearAndMonth) =
        appDatabase.transactionsDao()
            .getAllWithCategoryByMonth("${yearAndMonth}%").map { transactions ->
                //after getting all transactions, group them by date
                transactions.groupBy { transaction -> transaction.date }
                    .entries.map { entry ->
                        //then for each group, sort the list of transactions by created time
                        //so groups are sorted by date and transactions are sorted by created time
                        val sortedList = entry.value.sortedByDescending { it.createdAt }
                        TransactionGroup(sortedList, entry.key)
                    }
            }

    suspend fun saveTransaction(transaction: TransactionEntity) {
        withContext(dispatcher) {
            appDatabase.transactionsDao().insert(transaction)
        }
    }

    fun totalAmountForMonthAsFlow(yearAndMonth: YearAndMonth): Flow<Int> =
        appDatabase.transactionsDao().totalAmountForMonthAsFlow("$yearAndMonth%")

    suspend fun getCategoriesByType(type: TransactionType): List<CategoryEntity> {
        return withContext(dispatcher) {
            appDatabase.categoryDao().getAllNotDeletedByType(type)
        }
    }

}