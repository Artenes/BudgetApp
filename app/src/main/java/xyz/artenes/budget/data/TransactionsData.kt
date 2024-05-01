package xyz.artenes.budget.data

import xyz.artenes.budget.app.transaction.list.TransactionGroupItem
import xyz.artenes.budget.core.Money

data class TransactionsData(
    val totalExpenses: Money,
    val formattedTotalExpenses: String,
    val totalIncome: Money,
    val formattedTotalIncome: String,
    val balance: Money,
    val formattedBalance: String,
    val totalTransactions: Int,
    val groups: List<TransactionGroupItem>
)