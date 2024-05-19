package xyz.artenes.budget.data.models

import androidx.compose.ui.graphics.vector.ImageVector
import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.RawQuery
import androidx.room.Update
import androidx.sqlite.db.SupportSQLiteQuery
import kotlinx.coroutines.flow.Flow
import xyz.artenes.budget.core.models.Money
import xyz.artenes.budget.core.models.TransactionType
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

data class TransactionAndCategory(
    @Embedded(prefix = "trans_")
    val transaction: TransactionEntity,
    @Embedded(prefix = "cate_")
    val category: CategoryEntity
)

@Dao
interface TransactionDao {

    @Query("SELECT * FROM transactions ORDER BY created_at DESC")
    fun getAll(): Flow<List<TransactionEntity>>

    @Query("DELETE FROM transactions WHERE id = :id")
    suspend fun deleteById(id: UUID)

    @Query(
        "SELECT t.id as trans_id, " +
                "t.description as trans_description, " +
                "t.amount as trans_amount, " +
                "t.date as trans_date, " +
                "t.type as trans_type, " +
                "t.category_id as trans_category_id, " +
                "t.created_at as trans_created_at, " +
                "c.id as cate_id, " +
                "c.name as cate_name, " +
                "c.color as cate_color, " +
                "c.icon as cate_icon, " +
                "c.type as cate_type, " +
                "c.created_at as cate_created_at, " +
                "c.deleted_at as cate_deleted_at " +
                "FROM transactions t " +
                "INNER JOIN categories c ON category_id = c.id " +
                "WHERE t.id = :id "
    )
    fun getById(id: UUID): TransactionAndCategory

    @RawQuery
    fun search(query: SupportSQLiteQuery): List<TransactionWithCategoryEntity>

    @Query(
        "SELECT transactions.id, description, amount, date, transactions.type, color, icon, transactions.created_at " +
                "FROM transactions " +
                "INNER JOIN categories ON category_id = categories.id " +
                "WHERE date >= :startInclusive AND date <= :endInclusive " +
                "ORDER BY transactions.date DESC"
    )
    fun getByRange(
        startInclusive: LocalDate,
        endInclusive: LocalDate
    ): Flow<List<TransactionWithCategoryEntity>>

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
                "AND description LIKE :query " +
                "ORDER BY transactions.date DESC"
    )
    fun getByDay(yearMonthDay: LocalDate, query: String): Flow<List<TransactionWithCategoryEntity>>

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

    @Update
    suspend fun update(transaction: TransactionEntity)

    @Query("SELECT EXISTS(SELECT 1 FROM transactions WHERE id = :id)")
    suspend fun exists(id: UUID): Boolean

    @Insert
    suspend fun insertAll(transactions: List<TransactionEntity>)

    @Query("DELETE FROM transactions")
    suspend fun deleteAll()

}
