package xyz.artenes.budget.app.theme

import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.InputChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor

object CustomColorScheme {

    @Composable
    fun border() = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)

    @Composable
    fun solidBorder() = MaterialTheme.colorScheme.onBackground

    @Composable
    fun labelBackground() = MaterialTheme.colorScheme.background

    @Composable
    fun iconBackground(selected: Boolean) =
        if (selected) MaterialTheme.colorScheme.primary else Color.Transparent

    @Composable
    fun dropDownMenu() = MaterialTheme.colorScheme.tertiary

    @Composable
    fun statusBar() = MaterialTheme.colorScheme.primary

    @Composable
    fun statusBarDark() = MaterialTheme.colorScheme.tertiaryContainer

    @Composable
    fun backgroundDark() = MaterialTheme.colorScheme.tertiaryContainer

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun topAppBar() = TopAppBarDefaults.topAppBarColors().copy(
        containerColor = MaterialTheme.colorScheme.background
    )

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun topAppBarDark() = TopAppBarDefaults.topAppBarColors()
        .copy(containerColor = MaterialTheme.colorScheme.tertiaryContainer);

    @Composable
    fun icon() = MaterialTheme.colorScheme.onBackground

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun datePickerItem(selected: Boolean) =
        if (selected) MaterialTheme.colorScheme.primary else DatePickerDefaults.colors().containerColor

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
    fun chipBorder(enabled: Boolean = true, selected: Boolean = false) =
        FilterChipDefaults.filterChipBorder(
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
    fun textColor(alpha: Float = 1F) = MaterialTheme.colorScheme.onBackground.copy(alpha = alpha)

    @Composable
    fun textColorLight() = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)

    @Composable
    fun textColorExtraLight() = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.2f)

    @Composable
    fun divider() = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.2f)

}