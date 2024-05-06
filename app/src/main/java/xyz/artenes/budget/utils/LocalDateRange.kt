package xyz.artenes.budget.utils

import java.time.LocalDate
import java.time.temporal.TemporalAdjusters

data class LocalDateRange(
    val startInclusive: LocalDate,
    val endInclusive: LocalDate
) {

    val year: Int
        get() = startInclusive.year

    val month: Int
        get() = startInclusive.monthValue

    override fun toString(): String {
        return "${startInclusive}_$endInclusive"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as LocalDateRange

        if (startInclusive != other.startInclusive) return false
        if (endInclusive != other.endInclusive) return false

        return true
    }

    override fun hashCode(): Int {
        var result = startInclusive.hashCode()
        result = 31 * result + endInclusive.hashCode()
        return result
    }


    companion object {

        fun today(): LocalDateRange {
            val now = LocalDate.now()
            return LocalDateRange(now, now)
        }

        fun currentMonth(): LocalDateRange {
            val now = LocalDate.now();
            return LocalDateRange(
                now.with(TemporalAdjusters.firstDayOfMonth()),
                now.with(TemporalAdjusters.lastDayOfMonth())
            )
        }

        fun fromString(value: String): LocalDateRange {
            val parts = value.split("_")
            return LocalDateRange(LocalDate.parse(parts[0]), LocalDate.parse(parts[1]))
        }

        fun weeksInYearMonth(date: LocalDate): List<LocalDateRange> {

            val weeks = mutableListOf<LocalDateRange>()

            val firstDay = date.withDayOfMonth(1);
            val lastDay = date.withDayOfMonth(date.lengthOfMonth())
            val week = firstDay.dayOfWeek!!.value

            var start = if (week != 7) firstDay.minusDays(week.toLong()) else firstDay
            var end = start.plusDays(6)

            while (start <= lastDay) {
                weeks.add(LocalDateRange(start, end))
                start = end.plusDays(1)
                end = start.plusDays(6)
            }

            return weeks

        }

    }

}