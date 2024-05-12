package xyz.artenes.budget.core.models

import xyz.artenes.budget.data.models.TransactionWithCategoryEntity
import java.time.LocalDate

data class TransactionGroup(
    val transactions: List<TransactionWithCategoryEntity>,
    val date: LocalDate
)