package xyz.artenes.budget.core.models

import androidx.compose.ui.graphics.vector.ImageVector
import java.time.LocalDate
import java.util.UUID

data class TransactionItem(
    val id: UUID,
    val icon: ImageVector,
    val description: String,
    val colorAlpha: Float,
    val formattedAmount: String,
    val amount: Money,
    val type: TransactionType,
    val date: LocalDate,
    val formattedDate: String
)
