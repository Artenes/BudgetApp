package xyz.artenes.budget.data

import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
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
    @ColumnInfo("created_at")
    val createdAt: OffsetDateTime
)

@Dao
interface TransactionDao {

    @Query("SELECT * FROM transactions ORDER BY created_at DESC")
    fun getAll(): Flow<List<TransactionEntity>>

    @Query("SELECT SUM(amount) FROM transactions WHERE date LIKE :yearAndMonth")
    fun totalAmountForMonthAsFlow(yearAndMonth: String): Flow<Int>

    @Insert
    suspend fun insert(transaction: TransactionEntity)

}
