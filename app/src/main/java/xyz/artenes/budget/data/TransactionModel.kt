package xyz.artenes.budget.data

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey
    val id: String,
    val description: String,
    val amount: Int,
    val date: String
)

@Dao
interface TransactionDao {

    @Query("SELECT * FROM transactions ORDER BY date DESC")
    fun getAll(): Flow<List<TransactionEntity>>

    @Query("SELECT SUM(amount) FROM transactions WHERE date LIKE :yearAndMonth")
    suspend fun totalAmountForMonth(yearAndMonth: String): Int

    @Insert
    suspend fun insert(transaction: TransactionEntity)

}
