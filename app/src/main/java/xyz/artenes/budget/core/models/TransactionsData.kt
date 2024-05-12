package xyz.artenes.budget.core.models

import xyz.artenes.budget.core.models.TransactionGroupItem
import xyz.artenes.budget.core.models.Money

data class TransactionsData(
    val formattedCurrentMonth: String,
    val totalExpenses: Money,
    val formattedTotalExpenses: String,
    val totalIncome: Money,
    val formattedTotalIncome: String,
    val balance: Money,
    val formattedBalance: String,
    val totalTransactions: Int,
    val groups: List<TransactionGroupItem>
)