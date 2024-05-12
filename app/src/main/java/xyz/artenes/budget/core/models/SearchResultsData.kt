package xyz.artenes.budget.core.models

import xyz.artenes.budget.core.models.TransactionItem
import xyz.artenes.budget.core.models.Money

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