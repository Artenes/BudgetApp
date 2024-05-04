package xyz.artenes.budget.app.transaction.search

import xyz.artenes.budget.utils.LocalDateRange

data class DateFilterItem(
    val type: DateFilterType,
    val label: String,
    val value: LocalDateRange,
)

enum class DateFilterType {
    DAY,
    WEEK,
    MONTH,
    YEAR,
    CUSTOM
}