package xyz.artenes.budget.data

import java.time.LocalDate

data class TransactionGroup(
    val transactions: List<TransactionWithCategoryEntity>,
    val date: LocalDate
)