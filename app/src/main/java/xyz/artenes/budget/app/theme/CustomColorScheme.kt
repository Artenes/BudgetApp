package xyz.artenes.budget.app.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.runtime.Composable

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

}