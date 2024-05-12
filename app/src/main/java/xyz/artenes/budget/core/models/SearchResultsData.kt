package xyz.artenes.budget.core.models

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