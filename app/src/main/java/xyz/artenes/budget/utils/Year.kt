package xyz.artenes.budget.utils

import java.time.LocalDate

data class Year(val year: Int) {

    override fun toString(): String {
        return year.toString()
    }

    fun toLocalDate(): LocalDate {
        return LocalDate.of(year, 1, 1)
    }

    companion object {

        fun fromLocalDate(localDate: LocalDate): Year {
            return Year(localDate.year)
        }

        fun fromString(value: String): Year {
            return Year(value.toInt())
        }

    }

}