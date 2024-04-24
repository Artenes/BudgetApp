package xyz.artenes.budget.utils

import java.time.LocalDate

data class YearAndMonth(val year: Int, val month: Int) {

    override fun toString(): String {
        return "$year-${String.format("%02d", month)}"
    }

    companion object {

        fun fromLocalDate(localDate: LocalDate): YearAndMonth {
            return YearAndMonth(localDate.year, localDate.monthValue)
        }

        fun fromString(value: String): YearAndMonth {
            val parts = value.split("-")
            return YearAndMonth(parts[0].toInt(), parts[1].toInt())
        }

    }

}