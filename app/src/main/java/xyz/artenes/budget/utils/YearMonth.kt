package xyz.artenes.budget.utils

import java.time.LocalDate

data class YearMonth(val year: Int, val month: Int) {

    override fun toString(): String {
        return "$year-${String.format("%02d", month)}"
    }

    fun toLocalDate(): LocalDate {
        return LocalDate.of(year, month, 1)
    }

    companion object {

        fun fromLocalDate(localDate: LocalDate): YearMonth {
            return YearMonth(localDate.year, localDate.monthValue)
        }

        fun fromString(value: String): YearMonth {
            val parts = value.split("-")
            return YearMonth(parts[0].toInt(), parts[1].toInt())
        }

        fun now(): YearMonth {
            val now = LocalDate.now()
            return YearMonth(now.year, now.monthValue)
        }

    }

}