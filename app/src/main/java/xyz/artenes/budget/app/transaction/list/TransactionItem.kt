package xyz.artenes.budget.app.transaction.list

import androidx.compose.ui.graphics.vector.ImageVector
import xyz.artenes.budget.core.Money
import xyz.artenes.budget.core.TransactionType
import java.util.UUID

data class TransactionItem(
    val id: UUID,
    val icon: ImageVector,
    val description: String,
    val colorAlpha: Float,
    val formattedAmount: String,
    val amount: Money,
    val type: TransactionType
)
