package xyz.artenes.budget.data

import androidx.compose.ui.graphics.vector.ImageVector
import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import xyz.artenes.budget.core.TransactionType
import java.time.LocalDate
import java.time.OffsetDateTime
import java.util.UUID

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey
    val id: UUID,
    val description: String,
    val amount: Int,
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
    val amount: Int,
    val date: LocalDate,
    val type: TransactionType,
    val color: Int,
    val icon: ImageVector
)

@Dao
interface TransactionDao {

    @Query("SELECT * FROM transactions ORDER BY created_at DESC")
    fun getAll(): Flow<List<TransactionEntity>>

    @Query("SELECT transactions.id, description, amount, date, transactions.type, color, icon FROM transactions INNER JOIN categories ON category_id = categories.id ORDER BY transactions.created_at DESC")
    fun getAllWithCategory(): Flow<List<TransactionWithCategoryEntity>>

    @Query("SELECT SUM(amount) FROM transactions WHERE date LIKE :yearAndMonth")
    fun totalAmountForMonthAsFlow(yearAndMonth: String): Flow<Int>

    @Insert
    suspend fun insert(transaction: TransactionEntity)

}
