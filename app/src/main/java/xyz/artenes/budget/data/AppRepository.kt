package xyz.artenes.budget.data

import androidx.sqlite.db.SimpleSQLiteQuery
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import timber.log.Timber
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

    suspend fun search(
        range: LocalDateRange,
        sort: SearchSortType,
        category: CategoryEntity? = null,
        type: TransactionType? = null
    ): List<TransactionWithCategoryEntity> {
        return withContext(dispatcher) {

            val args = mutableListOf(
                dateSerializer.serializeDate(range.startInclusive),
                dateSerializer.serializeDate(range.endInclusive)
            )
            var categoryClause = ""
            var typeClause = ""
            var sortClause = ""

            if (category != null) {
                categoryClause = "AND categories.id = ? "
                args.add(category.id.toString())
            }

            if (type != null) {
                typeClause = "AND transactions.type = ? "
                args.add(type.toString())
            }

            sortClause = when (sort) {
                SearchSortType.DATE_ASC -> "ORDER BY transactions.date ASC"
                SearchSortType.DATE_DESC -> "ORDER BY transactions.date DESC"
                SearchSortType.NAME_ASC -> "ORDER BY transactions.description ASC"
                SearchSortType.NAME_DESC -> "ORDER BY transactions.description DESC"
                SearchSortType.VALUE_ASC -> "ORDER BY transactions.amount ASC"
                SearchSortType.VALUE_DESC -> "ORDER BY transactions.amount DESC"
            }

            val query = SimpleSQLiteQuery(
                "SELECT transactions.id, description, amount, date, transactions.type, color, icon, transactions.created_at " +
                        "FROM transactions " +
                        "INNER JOIN categories ON category_id = categories.id " +
                        "WHERE date >= ? AND date <= ? " +
                        categoryClause +
                        typeClause +
                        sortClause,
                args.toTypedArray()
            )

            Timber.d(query.sql)
            Timber.d(args.toString())

            appDatabase.transactionsDao().search(query)

        }
    }

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

    suspend fun getAllCategories(): List<CategoryEntity> {
        return withContext(dispatcher) {
            appDatabase.categoryDao().getAllNotDeleted()
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