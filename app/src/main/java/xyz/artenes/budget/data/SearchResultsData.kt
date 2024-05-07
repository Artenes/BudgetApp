package xyz.artenes.budget.data

import xyz.artenes.budget.app.transaction.list.TransactionItem
import xyz.artenes.budget.core.Money

data class SearchResultsData(
    val totalExpenses: Money,
    val formattedTotalExpenses: String,
    val totalIncome: Money,
    val formattedTotalIncome: String,
    val balance: Money,
    val formattedBalance: String,
    val balanceOpacity: Float,
    val totalTransactions: Int,
    val transactions: List<TransactionItem>
)