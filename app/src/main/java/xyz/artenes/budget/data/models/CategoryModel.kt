package xyz.artenes.budget.data.models

import androidx.compose.ui.graphics.vector.ImageVector
import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import xyz.artenes.budget.core.models.TransactionType
import java.time.OffsetDateTime
import java.util.UUID

@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey
    val id: UUID,
    val name: String,
    val color: Int,
    val icon: ImageVector,
    val type: TransactionType,
    @ColumnInfo("created_at")
    val createdAt: OffsetDateTime,
    @ColumnInfo("deleted_at")
    val deletedAt: OffsetDateTime?,
) {

    val isDeleted: Boolean
        get() = deletedAt != null

}

@Dao
interface CategoryDao {

    @Insert
    suspend fun insert(category: CategoryEntity)

    @Update
    suspend fun update(category: CategoryEntity)

    @Insert
    suspend fun insertAll(categories: List<CategoryEntity>)

    @Query("SELECT * FROM categories WHERE deleted_at IS NULL ORDER BY created_at DESC")
    suspend fun getAllNotDeleted(): List<CategoryEntity>

    @Query("SELECT * FROM categories WHERE deleted_at IS NULL ORDER BY created_at DESC")
    fun getAllNotDeletedAsFlow(): Flow<List<CategoryEntity>>

    @Query("SELECT * FROM categories WHERE deleted_at IS NULL AND type = :type ORDER BY name ASC")
    suspend fun getAllNotDeletedByType(type: TransactionType): List<CategoryEntity>

    @Query("SELECT * FROM categories WHERE deleted_at IS NULL AND id = :id")
    suspend fun getCategoryById(id: UUID): CategoryEntity

    @Query("SELECT EXISTS(SELECT 1 FROM categories LIMIT 1)")
    suspend fun hasEntries(): Boolean

    @Query("SELECT EXISTS(SELECT 1 FROM categories WHERE id = :id)")
    suspend fun exists(id: UUID): Boolean

    @Query("SELECT EXISTS(SELECT 1 FROM transactions t INNER JOIN categories c ON c.id = t.category_id WHERE c.id = :id AND c.deleted_at IS NULL)")
    suspend fun hasDependencies(id: UUID): Boolean

    @Query("SELECT COUNT(*) FROM categories WHERE type = :type AND deleted_at IS NULL")
    suspend fun countByType(type: TransactionType): Int

    @Query("UPDATE transactions SET category_id = :newCategory WHERE category_id = :oldCategory")
    suspend fun replaceCategoryInTransactions(oldCategory: UUID, newCategory: UUID)

}