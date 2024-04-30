package xyz.artenes.budget.app.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import xyz.artenes.budget.utils.DateRangeInclusive
import xyz.artenes.budget.utils.YearMonth
import java.time.Month
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomWeekPicker(
    visible: Boolean,
    value: DateRangeInclusive,
    onWeekSelected: (DateRangeInclusive) -> Unit,
    onDismiss: () -> Unit,
) {

    var years by remember {
        val middle = value.year
        val start = middle - 5
        val end = middle + 5
        mutableStateOf((start..end).mapIndexed { index, value ->
            YearItem(value, value == middle, index)
        }.toList())
    }

    var months by remember {
        val months = Month.entries.mapIndexed { index, month ->
            val label = month.getDisplayName(TextStyle.FULL, Locale.getDefault())
            val monthValue = month.value
            MonthItem(monthValue, label, monthValue == value.month, index)
        }
        mutableStateOf(months)
    }

    var weeks by remember {
        val weeks = makeWeeks(value.toYearMonth(), value)
        mutableStateOf(weeks)
    }

    val yearListState = rememberLazyListState(
        initialFirstVisibleItemIndex = years.first { it.selected }.position
    )

    val monthListState = rememberLazyListState(
        initialFirstVisibleItemIndex = months.first { it.selected }.position
    )

    if (visible) {
        BasicAlertDialog(onDismissRequest = onDismiss) {
            Surface(
                color = DatePickerDefaults.colors().containerColor,
                shape = DatePickerDefaults.shape,
                modifier = Modifier
                    .heightIn(max = 580.dp)
                    .requiredWidth(360.dp)
            ) {

                Column(
                    modifier = Modifier.padding(10.dp)
                ) {

                    /*
                    Years
                     */
                    LazyRow(
                        state = yearListState
                    ) {

                        items(
                            count = years.size,
                            key = { index -> years[index].value }
                        ) { index ->

                            val yearItem = years[index]

                            Surface(
                                color = if (yearItem.selected) MaterialTheme.colorScheme.primary else DatePickerDefaults.colors().containerColor,
                                onClick = {
                                    years =
                                        years.map { item -> item.copy(selected = yearItem == item) }
                                            .toList()
                                    val month = months.first { it.selected }.value
                                    val year = yearItem.value
                                    weeks = makeWeeks(YearMonth(year, month))
                                },
                                shape = MaterialTheme.shapes.medium
                            ) {
                                Text(
                                    modifier = Modifier.padding(10.dp),
                                    text = yearItem.value.toString()
                                )
                            }

                        }

                    }

                    /*
                    Months
                     */
                    LazyRow(
                        state = monthListState
                    ) {

                        items(
                            count = months.size,
                            key = { index -> months[index].value }
                        ) { index ->

                            val monthItem = months[index]

                            Surface(
                                color = if (monthItem.selected) MaterialTheme.colorScheme.primary else DatePickerDefaults.colors().containerColor,
                                onClick = {
                                    months =
                                        months.map { item -> item.copy(selected = monthItem == item) }
                                            .toList()
                                    val month = monthItem.value
                                    val year = years.first { it.selected }.value
                                    weeks = makeWeeks(YearMonth(year, month))
                                },
                                shape = MaterialTheme.shapes.medium
                            ) {
                                Text(
                                    modifier = Modifier.padding(10.dp),
                                    text = monthItem.label
                                )
                            }

                        }

                    }

                    HorizontalDivider()

                    /*
                    Weeks
                     */
                    LazyColumn(
                        modifier = Modifier.heightIn(max = 300.dp)
                    ) {

                        items(
                            count = weeks.size,
                            key = { index -> weeks[index].label }
                        ) { index ->

                            val weekItem = weeks[index]

                            Surface(
                                color = if (weekItem.selected) MaterialTheme.colorScheme.primary else DatePickerDefaults.colors().containerColor,
                                onClick = {
                                    weeks =
                                        weeks.map { item -> item.copy(selected = weekItem == item) }
                                            .toList()
                                },
                                shape = MaterialTheme.shapes.medium,
                                modifier = Modifier.fillMaxWidth()
                            ) {

                                Text(
                                    modifier = Modifier
                                        .padding(10.dp)
                                        .fillMaxWidth(),
                                    text = weekItem.label,
                                    textAlign = TextAlign.Center
                                )

                            }

                        }

                    }

                    Button(
                        onClick = {
                            onWeekSelected(weeks.first { it.selected }.value)
                            onDismiss()
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = weeks.find { it.selected } != null
                    ) {
                        Text(text = "Select week")
                    }

                }

            }
        }
    }

}

private fun makeWeeks(yearMonth: YearMonth, value: DateRangeInclusive? = null): List<WeekItem> {
    val dayFormat = DateTimeFormatter.ofPattern("dd MMMM")
    return DateRangeInclusive.weeksInYearMonth(yearMonth).mapIndexed { index, week ->
        WeekItem(
            week,
            "${week.start.format(dayFormat)} ~ ${week.end.format(dayFormat)}",
            week == value,
            index
        )
    }
}

private data class YearItem(
    val value: Int,
    val selected: Boolean,
    val position: Int
)

private data class MonthItem(
    val value: Int,
    val label: String,
    val selected: Boolean,
    val position: Int
)

private data class WeekItem(
    val value: DateRangeInclusive,
    val label: String,
    val selected: Boolean,
    val position: Int
)