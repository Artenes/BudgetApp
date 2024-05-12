package xyz.artenes.budget.core.serializer

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DateSerializer @Inject constructor() {

    private val dateSerializeFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    private val monthSerializeFormat = DateTimeFormatter.ofPattern("yyyy-MM")

    fun serializeDate(date: LocalDate): String = date.format(dateSerializeFormat)

    fun deserializeDate(date: String): LocalDate = LocalDate.parse(date, dateSerializeFormat)

    fun serializeYearAndMonth(date: LocalDate): String = date.format(monthSerializeFormat)

    fun deserializeYearAndMonth(yearAndMonth: String): LocalDate {
        val parts = yearAndMonth.split("-")
        val year = parts[0].toInt()
        val month = parts[1].toInt()
        return LocalDate.of(year, month, 1)
    }

}