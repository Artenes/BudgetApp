package xyz.artenes.budget.app.transaction.list

data class DateFilterItem(
    val type: DateFilterType,
    val label: String,
    val selected: Boolean = false
)

data class DateFilterValueItem(
    val type: DateFilterType,
    val label: String,
    val value: Any
)

enum class DateFilterType {
    DAY,
    WEEK,
    MONTH,
    YEAR,
    CUSTOM
}