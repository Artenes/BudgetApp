package xyz.artenes.budget.data

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalGroceryStore
import androidx.compose.material.icons.filled.Money
import androidx.compose.material.icons.filled.MonitorHeart
import androidx.compose.material.icons.filled.QuestionMark
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.ImageVector
import com.github.javafaker.Faker
import kotlinx.coroutines.withContext
import xyz.artenes.budget.R
import xyz.artenes.budget.android.Messages
import xyz.artenes.budget.core.Money
import xyz.artenes.budget.core.TransactionType
import java.time.LocalDate
import java.time.OffsetDateTime
import java.util.UUID
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

class DatabaseSeeder @Inject constructor(
    private val database: AppDatabase,
    private val dispatcher: CoroutineContext,
    private val messages: Messages,
    private val faker: Faker
) {

    suspend fun seed() {
        withContext(dispatcher) {
            seedCategories()
        }
    }

    suspend fun seedTest() {
        withContext(dispatcher) {
            seedCategories()
            seedTransactions()
        }
    }

    private suspend fun seedTransactions() {

        if (database.transactionsDao().hasEntries()) {
            return
        }

        if (!database.categoryDao().hasEntries()) {
            throw RuntimeException("Seed categories table before transactions table")
        }

        val items = mutableListOf<TransactionEntity>()
        val now = LocalDate.now();
        val start = LocalDate.of(now.year, 1, 1)
        val end = LocalDate.of(now.year, 12, 31)
        var currentDate = start

        val expenseCategories =
            database.categoryDao().getAllNotDeletedByType(TransactionType.EXPENSE)
        val incomeCategories = database.categoryDao().getAllNotDeletedByType(TransactionType.INCOME)
        val types = listOf(TransactionType.INCOME, TransactionType.EXPENSE)

        while (currentDate != end) {
            repeat(5) {
                val type = randomFromList(types)
                val category =
                    randomFromList(if (type == TransactionType.EXPENSE) expenseCategories else incomeCategories)

                items.add(
                    TransactionEntity(
                        id = UUID.randomUUID(),
                        description = faker.zelda().character()!!,
                        amount = Money(faker.number().numberBetween(10, 10000)),
                        date = currentDate,
                        type = type,
                        categoryId = category.id,
                        createdAt = OffsetDateTime.now()
                    )
                )
            }
            currentDate = currentDate.plusDays(1)
        }

        database.transactionsDao().insertAll(items)
    }

    private suspend fun seedCategories() {

        if (database.categoryDao().hasEntries()) {
            return
        }

        val categories = mutableListOf(
            makeCategory(
                R.string.category_transportation,
                Color.Blue,
                Icons.Filled.DirectionsBus,
                TransactionType.EXPENSE
            ),
            makeCategory(
                R.string.category_health,
                Color.Red,
                Icons.Filled.MonitorHeart,
                TransactionType.EXPENSE
            ),
            makeCategory(
                R.string.category_groceries,
                Color.Yellow,
                Icons.Filled.LocalGroceryStore,
                TransactionType.EXPENSE
            ),
            makeCategory(
                R.string.category_education,
                Color.Green,
                Icons.Filled.Book,
                TransactionType.EXPENSE
            ),
            makeCategory(
                R.string.category_home,
                Color.Cyan,
                Icons.Filled.Home,
                TransactionType.EXPENSE
            ),
            makeCategory(
                R.string.category_others,
                Color.Gray,
                Icons.Filled.QuestionMark,
                TransactionType.EXPENSE
            ),
            makeCategory(
                R.string.category_paycheck,
                Color.Blue,
                Icons.Filled.Money,
                TransactionType.INCOME
            ),
            makeCategory(
                R.string.category_others,
                Color.Gray,
                Icons.Filled.QuestionMark,
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
            color.toArgb(),
            icon,
            type,
            OffsetDateTime.now(),
            null
        )
    }

    private fun <T> randomFromList(items: List<T>): T {
        return items[faker.number().numberBetween(0, items.size)]
    }

}