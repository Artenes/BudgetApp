package xyz.artenes.budget.app.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import xyz.artenes.budget.R
import java.time.LocalDate
import java.time.Month
import java.time.format.TextStyle
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomMonthPicker(
    visible: Boolean,
    value: LocalDate? = null,
    onMonthSelected: (LocalDate) -> Unit,
    onDismiss: () -> Unit,
) {

    val coroutine = rememberCoroutineScope()

    var years by remember {
        mutableStateOf(makeYears(value))
    }

    var months by remember {
        mutableStateOf(makeMonths(value))
    }

    val yearListState = rememberLazyListState(
        initialFirstVisibleItemIndex = calculateInitialIndex(years.first { it.selected })
    )

    val monthListState = rememberLazyListState(
        initialFirstVisibleItemIndex = calculateInitialIndex(months.first { it.value == LocalDate.now().monthValue })
    )

    val dismissDialog = {
        years = makeYears(value)
        months = makeMonths(value)
        coroutine.launch {
            yearListState.scrollToItem(calculateInitialIndex(years.first { it.selected }))
            monthListState.scrollToItem(calculateInitialIndex(months.first { it.value == LocalDate.now().monthValue }))
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
                    }

                    /*
                    Months
                     */
                    DateRow(state = monthListState, items = months) { monthItem ->
                        months =
                            months.map { item -> item.copy(selected = monthItem == item) }.toList()
                    }

                    Button(
                        onClick = {
                            val year = years.first { it.selected }.value
                            val month = months.first { it.selected }.value
                            onMonthSelected(LocalDate.of(year, month, 1))
                            dismissDialog()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 20.dp),
                        enabled = years.find { it.selected } != null && months.find { it.selected } != null
                    ) {
                        Text(text = stringResource(R.string.select_month))
                    }

                }

            }
        }
    }

}

private fun calculateInitialIndex(item: DateItem): Int {
    val intendedPosition = item.position - 2
    return if (intendedPosition >= 0) {
        intendedPosition
    } else {
        0
    }
}

private fun makeMonths(value: LocalDate?): List<DateItem> {
    val months = Month.entries.mapIndexed { index, month ->
        val label = month.getDisplayName(TextStyle.FULL, Locale.getDefault())
        val monthValue = month.value
        DateItem(monthValue, monthValue == value?.month?.value, label, index)
    }
    return months
}

private fun makeYears(value: LocalDate?): List<DateItem> {
    val realValue = value ?: LocalDate.now()
    val middle = realValue.year
    val start = middle - 5
    val end = middle + 5
    return (start..end).mapIndexed { index, item ->
        DateItem(item, item == middle, item.toString(), index)
    }.toList()
}

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
                color = if (dateItem.selected) MaterialTheme.colorScheme.primary else DatePickerDefaults.colors().containerColor,
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