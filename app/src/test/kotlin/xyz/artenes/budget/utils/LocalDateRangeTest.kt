package xyz.artenes.budget.utils

import org.junit.Assert.assertEquals
import org.junit.Test
import xyz.artenes.budget.core.models.LocalDateRange
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

class LocalDateRangeTest {

    @Test
    fun weeksInYearMonth_returnRightValues() {

        validateNumberOfWeeks(1, 5)
        validateNumberOfWeeks(2, 5)
        validateNumberOfWeeks(3, 6)
        validateNumberOfWeeks(4, 5)
        validateNumberOfWeeks(5, 5)
        validateNumberOfWeeks(6, 6)
        validateNumberOfWeeks(7, 5)
        validateNumberOfWeeks(8, 5)
        validateNumberOfWeeks(9, 5)
        validateNumberOfWeeks(10, 5)
        validateNumberOfWeeks(11, 5)
        validateNumberOfWeeks(12, 5)

    }

    private fun validateNumberOfWeeks(month: Int, numberOfWeeks: Int) {
        val yearMonth = LocalDate.of(2024, month, 1)
        val monthName = yearMonth.month.getDisplayName(TextStyle.FULL, Locale.getDefault())
        val weeks = LocalDateRange.weeksInYearMonth(yearMonth)
        assertEquals("$monthName has wrong number of weeks $weeks", numberOfWeeks, weeks.size)
    }

}