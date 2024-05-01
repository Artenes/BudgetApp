package xyz.artenes.budget.utils

import xyz.artenes.budget.R
import xyz.artenes.budget.android.Messages
import xyz.artenes.budget.core.Money
import java.text.NumberFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.time.temporal.ChronoUnit
import java.util.Currency
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocaleFormatter @Inject constructor(private val messages: Messages) {

    private val dayOfWeekFormat = DateTimeFormatter.ofPattern("EEEE")
    private val serializedDate = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    fun getDateFormat() = DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT)

    fun getMoneyFormat() = NumberFormat.getNumberInstance().also {
        it.minimumFractionDigits = 2
        it.maximumFractionDigits = 2
    }

    fun getCurrencySymbol() = Currency.getInstance(Locale.getDefault()).symbol

    fun formatMoney(money: Money): String {
        return getMoneyFormat().format(money.toDouble)
    }

    fun formatMoneyWithCurrency(money: Money): String {
        return "${getCurrencySymbol()} ${getMoneyFormat().format(money.toDouble)}"
    }

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

    fun formatDateAsRelative(date: LocalDate): RelativeDate {
        val today = LocalDate.now()
        val daysBetween = ChronoUnit.DAYS.between(date, today)

        val formattedDate = date.format(getDateFormat())
        return when (daysBetween) {
            0L -> RelativeDate(messages.get(R.string.today), formattedDate)
            1L -> RelativeDate(messages.get(R.string.yesterday), formattedDate)
            in 2..6 -> RelativeDate(date.format(dayOfWeekFormat), formattedDate)
            else -> RelativeDate("", formattedDate)
        }
    }

}