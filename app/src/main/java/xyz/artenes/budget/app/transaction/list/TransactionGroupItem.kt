package xyz.artenes.budget.app.transaction.list

import xyz.artenes.budget.utils.RelativeDate
import java.time.LocalDate

data class TransactionGroupItem(
    val key: LocalDate,
    val date: RelativeDate,
    val transactions: List<TransactionItem>
)
