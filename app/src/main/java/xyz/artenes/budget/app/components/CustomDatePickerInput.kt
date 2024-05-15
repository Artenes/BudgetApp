package xyz.artenes.budget.app.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import xyz.artenes.budget.app.theme.CustomColorScheme
import xyz.artenes.budget.R
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

private val dateFormat = DateTimeFormatter.ISO_DATE

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomDatePickerInput(
    label: String,
    value: LocalDate,
    onDateSelected: (LocalDate) -> Unit
) {

    val focusManager = LocalFocusManager.current
    var showDatePickerDialog by remember {
        mutableStateOf(false)
    }

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = OffsetDateTime.of(value, LocalTime.now(), ZoneOffset.UTC)
            .toInstant().toEpochMilli()
    )

    if (showDatePickerDialog) {
        DatePickerDialog(
            onDismissRequest = { showDatePickerDialog = false },
            confirmButton = {
                Button(
                    onClick = {
                        datePickerState.selectedDateMillis?.let {
                            onDateSelected(
                                Instant.ofEpochMilli(it).atZone(ZoneId.of("UTC"))
                                    .toLocalDate()
                            )
                        }
                        showDatePickerDialog = false
                    },
                ) {
                    Text(
                        text = stringResource(id = R.string.select_date),
                        color = CustomColorScheme.textColor()
                    )
                }
            },
        ) {
            DatePicker(state = datePickerState)
        }
    }

    OutlinedTextField(
        value = value.format(dateFormat),
        onValueChange = { },
        modifier = Modifier
            .fillMaxWidth()
            .onFocusEvent {
                if (it.isFocused) {
                    showDatePickerDialog = true
                    focusManager.clearFocus(force = true)
                }
            },
        colors = CustomColorScheme.outlineTextField(),
        label = {
            Text(label)
        },
        trailingIcon = {
            Icon(
                imageVector = Icons.Filled.CalendarMonth,
                contentDescription = "",
                tint = CustomColorScheme.icon()
            )
        },
        readOnly = true
    )

}