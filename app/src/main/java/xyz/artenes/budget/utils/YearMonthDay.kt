package xyz.artenes.budget.utils

import java.time.LocalDate

data class YearMonthDay(val year: Int, val month: Int, val day: Int) {

    override fun toString(): String {
        return "$year-${String.format("%02d", month)}-${String.format("%02d", day)}"
    }

    fun toLocalDate(): LocalDate {
        return LocalDate.of(year, month, day)
    }

    companion object {

        fun fromLocalDate(localDate: LocalDate): YearMonthDay {
            return YearMonthDay(localDate.year, localDate.monthValue, localDate.dayOfMonth)
        }

        fun fromString(value: String): YearMonthDay {
            val parts = value.split("-")
            return YearMonthDay(parts[0].toInt(), parts[1].toInt(), parts[2].toInt())
        }

    }

}