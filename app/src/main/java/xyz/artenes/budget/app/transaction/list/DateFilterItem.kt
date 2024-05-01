package xyz.artenes.budget.app.transaction.list

import xyz.artenes.budget.utils.DateRangeInclusive
import xyz.artenes.budget.utils.YearMonthDay

data class DateFilterItem(
    val type: DateFilterType,
    val label: String,
    val selected: Boolean = false
)

data class DateFilterValueItem(
    val type: DateFilterType,
    val value: String,
    val label: String
) {

    val key: String = "${type}_VALUE"

    fun toLocalDate() = YearMonthDay.fromString(value).toLocalDate()

    fun toWeek() = DateRangeInclusive.fromString(value)

}

enum class DateFilterType {
    DAY,
    WEEK,
    MONTH,
    YEAR,
    CUSTOM
}