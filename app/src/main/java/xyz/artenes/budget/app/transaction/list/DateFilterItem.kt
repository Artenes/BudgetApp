package xyz.artenes.budget.app.transaction.list

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

}

enum class DateFilterType {
    DAY,
    WEEK,
    MONTH,
    YEAR,
    CUSTOM
}