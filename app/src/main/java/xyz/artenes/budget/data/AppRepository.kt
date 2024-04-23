package xyz.artenes.budget.data

import kotlinx.coroutines.flow.Flow
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

    fun getAllTransactionsWithCategory() = appDatabase.transactionsDao().getAllWithCategory()

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