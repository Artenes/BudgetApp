package xyz.artenes.budget.data

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Book
import androidx.compose.material.icons.outlined.DirectionsBus
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.LocalGroceryStore
import androidx.compose.material.icons.outlined.Money
import androidx.compose.material.icons.outlined.MonitorHeart
import androidx.compose.material.icons.outlined.QuestionMark
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import kotlinx.coroutines.withContext
import xyz.artenes.budget.R
import xyz.artenes.budget.android.Messages
import xyz.artenes.budget.core.TransactionType
import java.time.OffsetDateTime
import java.util.UUID
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

class DatabaseSeeder @Inject constructor(
    private val database: AppDatabase,
    private val dispatcher: CoroutineContext,
    private val messages: Messages
) {

    suspend fun seed() {
        withContext(dispatcher) {
            seedCategories()
        }
    }

    private suspend fun seedCategories() {

        if (database.categoryDao().hasEntries()) {
            return
        }

        val categories = mutableListOf(
            makeCategory(
                R.string.category_transportation,
                Color.Blue,
                Icons.Outlined.DirectionsBus,
                TransactionType.EXPENSE
            ),
            makeCategory(
                R.string.category_health,
                Color.Red,
                Icons.Outlined.MonitorHeart,
                TransactionType.EXPENSE
            ),
            makeCategory(
                R.string.category_groceries,
                Color.Yellow,
                Icons.Outlined.LocalGroceryStore,
                TransactionType.EXPENSE
            ),
            makeCategory(
                R.string.category_education,
                Color.Green,
                Icons.Outlined.Book,
                TransactionType.EXPENSE
            ),
            makeCategory(
                R.string.category_home,
                Color.Cyan,
                Icons.Outlined.Home,
                TransactionType.EXPENSE
            ),
            makeCategory(
                R.string.category_others,
                Color.Gray,
                Icons.Outlined.QuestionMark,
                TransactionType.EXPENSE
            ),
            makeCategory(
                R.string.category_paycheck,
                Color.Blue,
                Icons.Outlined.Money,
                TransactionType.INCOME
            ),
            makeCategory(
                R.string.category_others,
                Color.Gray,
                Icons.Outlined.QuestionMark,
                TransactionType.INCOME
            )
        )

        database.categoryDao().insertAll(categories)
    }

    private fun makeCategory(
        id: Int,
        color: Color,
        icon: ImageVector,
        type: TransactionType
    ): CategoryEntity {
        return CategoryEntity(
            UUID.randomUUID(),
            messages.get(id),
            color.value.toLong(),
            icon,
            type,
            OffsetDateTime.now(),
            null
        )
    }

}