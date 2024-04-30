package xyz.artenes.budget.app.components

import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.ZoneOffset

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomDatePicker(
    visible: Boolean,
    value: LocalDate,
    onDateSelected: (LocalDate) -> Unit,
    onDismiss: () -> Unit,
) {

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = OffsetDateTime.of(value, LocalTime.now(), ZoneOffset.UTC)
            .toInstant().toEpochMilli()
    )

    if (visible) {
        DatePickerDialog(
            onDismissRequest = onDismiss,
            confirmButton = {
                Button(
                    onClick = {
                        datePickerState.selectedDateMillis?.let {
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
            DatePicker(state = datePickerState)
        }
    }

}