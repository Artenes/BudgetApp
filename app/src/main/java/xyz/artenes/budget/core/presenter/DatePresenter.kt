package xyz.artenes.budget.core.presenter

import xyz.artenes.budget.R
import xyz.artenes.budget.core.models.LocalDateRange
import xyz.artenes.budget.core.Messages
import xyz.artenes.budget.core.models.RelativeDate
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.time.temporal.ChronoUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DatePresenter @Inject constructor(private val androidMessages: Messages) {

    private val dayOfWeekFormat = DateTimeFormatter.ofPattern("EEEE")

    private fun getDateFormat(): DateTimeFormatter =
        DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT)

    fun formatDate(date: LocalDate): String {
        return date.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT))
    }

    fun formatRange(range: LocalDateRange): String {
        val format = DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT)
        return "${range.startInclusive.format(format)} ~ ${range.endInclusive.format(format)}"
    }

    fun formatMonthAndYear(date: LocalDate): String {
        return date.format(DateTimeFormatter.ofPattern("MMMM yyyy"))
    }

    fun formatMonth(date: LocalDate): String {
        return date.format(DateTimeFormatter.ofPattern("MMMM"))
    }

    fun formatDateAsRelative(date: LocalDate): RelativeDate {
        val today = LocalDate.now()
        val daysBetween = ChronoUnit.DAYS.between(date, today)

        val formattedDate = date.format(getDateFormat())
        return when (daysBetween) {
            0L -> RelativeDate(androidMessages.get(R.string.today), formattedDate)
            1L -> RelativeDate(androidMessages.get(R.string.yesterday), formattedDate)
            in 2..6 -> RelativeDate(date.format(dayOfWeekFormat), formattedDate)
            else -> RelativeDate("", formattedDate)
        }
    }

}