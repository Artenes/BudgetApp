package xyz.artenes.budget.app.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.DateRangePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import xyz.artenes.budget.R
import xyz.artenes.budget.app.theme.CustomColorScheme
import xyz.artenes.budget.utils.LocalDateRange
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomAdvancedDatePicker() {

    val focusManager = LocalFocusManager.current
    var showDialog by remember {
        mutableStateOf(false)
    }
    var showDayDialog by remember {
        mutableStateOf(false)
    }
    var showWeekDialog by remember {
        mutableStateOf(false)
    }
    var showMonthDialog by remember {
        mutableStateOf(false)
    }
    var showYearDialog by remember {
        mutableStateOf(false)
    }
    var showRangeDialog by remember {
        mutableStateOf(false)
    }
    val dayState = rememberDatePickerState()
    val rangeState = rememberDateRangePickerState()

    OutlinedTextField(
        value = "",
        onValueChange = { },
        modifier = Modifier
            .fillMaxWidth()
            .onFocusEvent {
                if (it.isFocused) {
                    showDialog = true
                    focusManager.clearFocus(force = true)
                }
            },
        colors = CustomColorScheme.outlineTextField(),
        label = {
            Text(stringResource(R.string.filter_by_date))
        },
        trailingIcon = {
            Icon(
                imageVector = Icons.Filled.CalendarMonth,
                contentDescription = "",
                tint = MaterialTheme.colorScheme.onBackground
            )
        },
        readOnly = true
    )

    DateTypeDialog(
        show = showDialog,
        onDismiss = { showDialog = false },
        onDaySelected = { showDayDialog = true },
        onWeekSelected = { showWeekDialog = true },
        onMonthSelected = { showMonthDialog = true },
        onYearSelected = { showYearDialog = true },
        onRangeSelected = { showRangeDialog = true },
    )

    DayDialog(
        show = showDayDialog,
        onDismiss = { showDayDialog = false },
        state = dayState,
        onDateSelected = { date -> }
    )

    CustomWeekPicker(
        visible = showWeekDialog,
        value = LocalDateRange.now(),
        onWeekSelected = { newWeek -> },
        onDismiss = { showWeekDialog = false }
    )

    CustomMonthPicker(
        visible = showMonthDialog,
        value = LocalDate.now(),
        onMonthSelected = { newMonth -> },
        onDismiss = { showMonthDialog = false }
    )

    CustomYearPicker(
        visible = showYearDialog,
        value = LocalDate.now().year,
        onYearSelected = { newYear -> },
        onDismiss = { showYearDialog = false }
    )

    RangeDialog(
        show = showRangeDialog,
        onDismiss = { showRangeDialog = false },
        state = rangeState,
        onDateSelected = { newRange -> }
    )

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DateTypeDialog(
    show: Boolean,
    onDismiss: () -> Unit,
    onDaySelected: () -> Unit,
    onWeekSelected: () -> Unit,
    onMonthSelected: () -> Unit,
    onYearSelected: () -> Unit,
    onRangeSelected: () -> Unit
) {

    if (!show) {
        return
    }

    BasicAlertDialog(onDismissRequest = onDismiss) {

        Surface(
            color = DatePickerDefaults.colors().containerColor,
            shape = DatePickerDefaults.shape,
            modifier = Modifier
                .heightIn(max = 580.dp)
                .requiredWidth(360.dp)
        ) {

            Column(
                modifier = Modifier.padding(20.dp)
            ) {

                DateTypeItem(
                    label = stringResource(id = R.string.filter_by_day),
                    onClick = {
                        onDaySelected()
                        onDismiss()
                    },
                )

                DateTypeItem(
                    label = stringResource(id = R.string.filter_by_week),
                    onClick = {
                        onWeekSelected()
                        onDismiss()
                    },
                    modifier = Modifier.padding(top = 10.dp)
                )

                DateTypeItem(
                    label = stringResource(id = R.string.filter_by_month),
                    onClick = {
                        onMonthSelected()
                        onDismiss()
                    },
                    modifier = Modifier.padding(top = 10.dp)
                )

                DateTypeItem(
                    label = stringResource(id = R.string.filter_by_year),
                    onClick = {
                        onYearSelected()
                        onDismiss()
                    },
                    modifier = Modifier.padding(top = 10.dp)
                )

                DateTypeItem(
                    label = stringResource(id = R.string.filter_by_custom_range),
                    onClick = {
                        onRangeSelected()
                        onDismiss()
                    },
                    modifier = Modifier.padding(top = 10.dp)
                )

            }

        }

    }

}

@Composable
private fun DateTypeItem(label: String, onClick: () -> Unit, modifier: Modifier = Modifier) {

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .then(modifier),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)),
        shape = MaterialTheme.shapes.medium,
        onClick = onClick
    ) {

        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            text = label,
            textAlign = TextAlign.Center,
            color = CustomColorScheme.textColor()
        )

    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DayDialog(
    show: Boolean,
    onDismiss: () -> Unit,
    state: DatePickerState,
    onDateSelected: (LocalDate) -> Unit
) {

    if (!show) {
        return
    }

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                onClick = {
                    state.selectedDateMillis?.let {
                        onDateSelected(
                            Instant.ofEpochMilli(it).atZone(ZoneId.of("UTC"))
                                .toLocalDate()
                        )
                    }
                    onDismiss()
                },
            ) {
                Text(text = "Select date")
            }
        },
    ) {
        DatePicker(state = state)
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RangeDialog(
    show: Boolean,
    onDismiss: () -> Unit,
    state: DateRangePickerState,
    onDateSelected: (LocalDateRange) -> Unit
) {

    if (!show) {
        return
    }

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                enabled = state.selectedStartDateMillis != null && state.selectedEndDateMillis != null,
                onClick = {
                    val start = Instant.ofEpochMilli(state.selectedStartDateMillis!!)
                        .atZone(ZoneId.of("UTC")).toLocalDate()
                    val end =
                        Instant.ofEpochMilli(state.selectedEndDateMillis!!).atZone(ZoneId.of("UTC"))
                            .toLocalDate()
                    onDateSelected(LocalDateRange(start, end))
                    onDismiss()
                },
            ) {
                Text(text = stringResource(R.string.select_range))
            }
        },
    ) {
        DateRangePicker(state = state)
    }

}