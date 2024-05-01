package xyz.artenes.budget.app.transaction.list

import xyz.artenes.budget.utils.DateRangeInclusive
import java.time.LocalDate

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

    fun toLocalDate() = LocalDate.parse(value)

    fun toWeek() = DateRangeInclusive.fromString(value)

}

enum class DateFilterType {
    DAY,
    WEEK,
    MONTH,
    YEAR,
    CUSTOM
}