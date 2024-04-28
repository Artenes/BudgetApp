package xyz.artenes.budget.data

import androidx.compose.ui.graphics.vector.ImageVector
import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import xyz.artenes.budget.core.Money
import xyz.artenes.budget.core.TransactionType
import xyz.artenes.budget.utils.Year
import xyz.artenes.budget.utils.YearMonthDay
import java.time.LocalDate
import java.time.OffsetDateTime
import java.util.UUID

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey
    val id: UUID,
    val description: String,
    val amount: Money,
    val date: LocalDate,
    val type: TransactionType,
    @ColumnInfo("category_id")
    val categoryId: UUID,
    @ColumnInfo("created_at")
    val createdAt: OffsetDateTime
)

data class TransactionWithCategoryEntity(
    val id: UUID,
    val description: String,
    val amount: Money,
    val date: LocalDate,
    val type: TransactionType,
    val color: Int,
    val icon: ImageVector,
    @ColumnInfo("created_at")
    val createdAt: OffsetDateTime
)

@Dao
interface TransactionDao {

    @Query("SELECT * FROM transactions ORDER BY created_at DESC")
    fun getAll(): Flow<List<TransactionEntity>>

    @Query(
        "SELECT transactions.id, description, amount, date, transactions.type, color, icon, transactions.created_at " +
                "FROM transactions " +
                "INNER JOIN categories ON category_id = categories.id " +
                "WHERE date LIKE :yearAndMonth " +
                "ORDER BY transactions.date DESC"
    )
    fun getByMonth(yearAndMonth: String): Flow<List<TransactionWithCategoryEntity>>

    @Query(
        "SELECT transactions.id, description, amount, date, transactions.type, color, icon, transactions.created_at " +
                "FROM transactions " +
                "INNER JOIN categories ON category_id = categories.id " +
                "WHERE date = :yearMonthDay " +
                "ORDER BY transactions.date DESC"
    )
    fun getByDay(yearMonthDay: LocalDate): Flow<List<TransactionWithCategoryEntity>>

    @Query(
        "SELECT transactions.id, description, amount, date, transactions.type, color, icon, transactions.created_at " +
                "FROM transactions " +
                "INNER JOIN categories ON category_id = categories.id " +
                "WHERE date LIKE :year " +
                "ORDER BY transactions.date DESC"
    )
    fun getByYear(year: String): Flow<List<TransactionWithCategoryEntity>>

    @Query("SELECT SUM(amount) FROM transactions WHERE date LIKE :yearAndMonth")
    fun totalAmountForMonthAsFlow(yearAndMonth: String): Flow<Int>

    @Query("SELECT EXISTS(SELECT 1 FROM transactions LIMIT 1)")
    suspend fun hasEntries(): Boolean

    @Insert
    suspend fun insert(transaction: TransactionEntity)

    @Insert
    suspend fun insertAll(transactions: List<TransactionEntity>)

}
