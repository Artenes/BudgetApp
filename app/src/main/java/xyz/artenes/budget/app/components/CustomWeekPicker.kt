package xyz.artenes.budget.app.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import xyz.artenes.budget.R
import xyz.artenes.budget.app.theme.CustomColorScheme
import xyz.artenes.budget.core.models.DateItem
import xyz.artenes.budget.core.models.LocalDateRange
import java.time.LocalDate
import java.time.Month
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomWeekPicker(
    visible: Boolean,
    value: LocalDateRange? = null,
    onWeekSelected: (LocalDateRange) -> Unit,
    onDismiss: () -> Unit,
) {

    val coroutine = rememberCoroutineScope()

    var years by remember {
        mutableStateOf(makeYears(value))
    }

    var months by remember {
        mutableStateOf(makeMonths(value))
    }

    var weeks by remember {
        mutableStateOf(makeWeeks(value?.startInclusive, value))
    }

    val yearListState = rememberLazyListState(
        initialFirstVisibleItemIndex = years.first { it.selected }.position
    )

    val monthListState = rememberLazyListState(
        initialFirstVisibleItemIndex = months.first { it.selected }.position
    )

    val dismissDialog = {
        years = makeYears(value)
        months = makeMonths(value)
        weeks = makeWeeks(value?.startInclusive, value)
        coroutine.launch {
            yearListState.scrollToItem(years.first { it.selected }.position)
            monthListState.scrollToItem(months.first { it.selected }.position)
        }
        onDismiss()
    }

    if (visible) {
        BasicAlertDialog(onDismissRequest = dismissDialog) {
            Surface(
                color = DatePickerDefaults.colors().containerColor,
                shape = DatePickerDefaults.shape,
                modifier = Modifier
                    .heightIn(max = 580.dp)
                    .requiredWidth(360.dp)
            ) {

                Column(
                    modifier = Modifier.padding(30.dp)
                ) {

                    /*
                    Years
                     */
                    DateRow(state = yearListState, items = years) { yearItem ->
                        years =
                            years.map { item -> item.copy(selected = yearItem == item) }.toList()
                        val month = months.first { it.selected }.value
                        val year = yearItem.value
                        weeks = makeWeeks(LocalDate.of(year, month, 1))
                    }

                    /*
                    Months
                     */
                    DateRow(state = monthListState, items = months) { monthItem ->
                        months =
                            months.map { item -> item.copy(selected = monthItem == item) }.toList()
                        val month = monthItem.value
                        val year = years.first { it.selected }.value
                        weeks = makeWeeks(LocalDate.of(year, month, 1))
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp))

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
                                color = CustomColorScheme.datePickerItem(selected = weekItem.selected),
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
                            dismissDialog()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 20.dp),
                        enabled = weeks.find { it.selected } != null
                    ) {
                        Text(text = stringResource(R.string.select_week))
                    }

                }

            }
        }
    }

}

private fun makeMonths(value: LocalDateRange?): List<DateItem> {
    val realValue = value ?: LocalDateRange.today()
    val months = Month.entries.mapIndexed { index, month ->
        val label = month.getDisplayName(TextStyle.FULL, Locale.getDefault())
        val monthValue = month.value
        DateItem(monthValue, monthValue == realValue.month, label, index)
    }
    return months
}

private fun makeYears(value: LocalDateRange?): List<DateItem> {
    val realValue = value ?: LocalDateRange.today()
    val middle = realValue.year
    val start = middle - 5
    val end = middle + 5
    return (start..end).mapIndexed { index, item ->
        DateItem(item, item == middle, item.toString(), index)
    }.toList()
}

private fun makeWeeks(date: LocalDate?, value: LocalDateRange? = null): List<WeekItem> {
    val realDate = date ?: LocalDate.now()
    val dayFormat = DateTimeFormatter.ofPattern("dd MMMM")
    return LocalDateRange.weeksInYearMonth(realDate).mapIndexed { index, week ->
        WeekItem(
            week,
            "${week.startInclusive.format(dayFormat)} ~ ${week.endInclusive.format(dayFormat)}",
            week == value,
            index
        )
    }
}

private data class WeekItem(
    val value: LocalDateRange,
    val label: String,
    val selected: Boolean,
    val position: Int
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DateRow(
    state: LazyListState,
    items: List<DateItem>,
    onClick: (DateItem) -> Unit
) {

    LazyRow(
        state = state
    ) {

        items(
            count = items.size,
            key = { index -> items[index].value }
        ) { index ->

            val dateItem = items[index]

            Surface(
                color = CustomColorScheme.datePickerItem(selected = dateItem.selected),
                onClick = {
                    onClick(dateItem)
                },
                shape = MaterialTheme.shapes.medium
            ) {
                Text(
                    modifier = Modifier.padding(10.dp),
                    text = dateItem.label
                )
            }

        }

    }
}