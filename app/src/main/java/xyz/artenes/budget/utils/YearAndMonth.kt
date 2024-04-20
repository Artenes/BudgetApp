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

    }

}