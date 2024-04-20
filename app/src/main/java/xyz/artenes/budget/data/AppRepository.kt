package xyz.artenes.budget.data

import kotlinx.coroutines.withContext
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

    suspend fun saveTransaction(transaction: TransactionEntity) {
        withContext(dispatcher) {
            appDatabase.transactionsDao().insert(transaction)
        }
    }

    suspend fun totalAmountForMonth(yearAndMonth: YearAndMonth): Int {
        return withContext(dispatcher) {
            appDatabase.transactionsDao().totalAmountForMonth("$yearAndMonth%")
        }
    }

}