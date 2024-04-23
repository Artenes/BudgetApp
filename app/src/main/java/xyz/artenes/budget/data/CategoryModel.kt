package xyz.artenes.budget.data

import androidx.compose.ui.graphics.vector.ImageVector
import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import xyz.artenes.budget.core.TransactionType
import java.time.OffsetDateTime
import java.util.UUID

@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey
    val id: UUID,
    val name: String,
    val color: Long,
    val icon: ImageVector,
    val type: TransactionType,
    @ColumnInfo("created_at")
    val createdAt: OffsetDateTime,
    @ColumnInfo("deleted_at")
    val deletedAt: OffsetDateTime?,
)

@Dao
interface CategoryDao {

    @Insert
    suspend fun insert(category: CategoryEntity)

    @Insert
    suspend fun insertAll(categories: List<CategoryEntity>)

    @Query("SELECT * FROM categories WHERE deleted_at IS NULL")
    suspend fun getAllNotDeleted(): List<CategoryEntity>

    @Query("SELECT EXISTS(SELECT 1 FROM categories LIMIT 1)")
    suspend fun hasEntries(): Boolean

}