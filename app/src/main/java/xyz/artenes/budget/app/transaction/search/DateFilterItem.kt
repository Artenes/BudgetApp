package xyz.artenes.budget.app.transaction.search

import xyz.artenes.budget.utils.LocalDateRange

data class DateFilter(
    val type: DateFilterType,
    val value: LocalDateRange,
)

data class DateFilterItem(
    val label: String,
    val value: DateFilter
)

enum class DateFilterType {
    DAY,
    WEEK,
    MONTH,
    YEAR,
    CUSTOM
}