package xyz.artenes.budget.utils

import java.time.LocalDate

data class DateRangeInclusive(
    val start: LocalDate,
    val end: LocalDate
) {

    val year: Int
        get() = start.year

    val month: Int
        get() = start.monthValue

    fun toYearMonth(): YearMonth {
        return YearMonth(year, month)
    }

    override fun toString(): String {
        return "${start}_$end"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as DateRangeInclusive

        if (start != other.start) return false
        if (end != other.end) return false

        return true
    }

    override fun hashCode(): Int {
        var result = start.hashCode()
        result = 31 * result + end.hashCode()
        return result
    }


    companion object {

        fun now(): DateRangeInclusive {
            val now = LocalDate.now()
            val week = now.dayOfWeek!!.value
            val start = now.minusDays(week.toLong())
            val end = start.plusDays(6)
            return DateRangeInclusive(start, end)
        }

        fun fromString(value: String): DateRangeInclusive {
            val parts = value.split("_")
            return DateRangeInclusive(LocalDate.parse(parts[0]), LocalDate.parse(parts[1]))
        }

        fun weeksInYearMonth(yearMonth: YearMonth): List<DateRangeInclusive> {

            val weeks = mutableListOf<DateRangeInclusive>()

            val now = yearMonth.toLocalDate()
            val firstDay = now.withDayOfMonth(1);
            val lastDay = now.withDayOfMonth(now.lengthOfMonth())
            val week = firstDay.dayOfWeek!!.value

            var start = if (week != 7) firstDay.minusDays(week.toLong()) else firstDay
            var end = start.plusDays(6)

            while (start <= lastDay) {
                weeks.add(DateRangeInclusive(start, end))
                start = end.plusDays(1)
                end = start.plusDays(6)
            }

            return weeks

        }

    }

}