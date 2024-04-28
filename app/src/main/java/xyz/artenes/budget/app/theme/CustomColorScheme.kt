package xyz.artenes.budget.app.theme

import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.InputChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.SolidColor

object CustomColorScheme {

    @Composable
    fun outlineTextField() = OutlinedTextFieldDefaults.colors().copy(
        focusedTextColor = MaterialTheme.colorScheme.onBackground,
        focusedLabelColor = MaterialTheme.colorScheme.onBackground,
        unfocusedLabelColor = MaterialTheme.colorScheme.onBackground,
        unfocusedIndicatorColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
        focusedIndicatorColor = MaterialTheme.colorScheme.onBackground,
        errorIndicatorColor = MaterialTheme.colorScheme.tertiary,
        cursorColor = MaterialTheme.colorScheme.onBackground,
    )

    @Composable
    fun chipBorder(enabled: Boolean, selected: Boolean) = FilterChipDefaults.filterChipBorder(
        enabled = enabled,
        selected = selected,
    ).copy(
        brush = SolidColor(MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f))
    )

    @Composable
    fun surfaceColor() = MaterialTheme.colorScheme.background

    @Composable
    fun filterChipColor() = FilterChipDefaults.filterChipColors().copy(
        labelColor = MaterialTheme.colorScheme.onBackground
    )

    @Composable
    fun inputChipColor() = InputChipDefaults.inputChipColors().copy(
        labelColor = MaterialTheme.colorScheme.onBackground,
        trailingIconColor = MaterialTheme.colorScheme.onBackground
    )

    @Composable
    fun textColor() = MaterialTheme.colorScheme.onBackground

}