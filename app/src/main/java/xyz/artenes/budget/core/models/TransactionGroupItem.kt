package xyz.artenes.budget.core.models

import java.time.LocalDate

data class TransactionGroupItem(
    val key: LocalDate,
    val date: RelativeDate,
    val transactions: List<TransactionItem>
)
