package xyz.artenes.budget.app.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import xyz.artenes.budget.app.theme.CustomColorScheme
import xyz.artenes.budget.core.models.SelectableItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> CustomSpinner(
    label: String,
    options: List<SelectableItem<T>>,
    onOptionSelected: (SelectableItem<T>) -> Unit,
    error: String? = null
) {

    var expanded by remember {
        mutableStateOf(false)
    }

    val resolveSupportText: (content: @Composable (() -> Unit)) -> @Composable (() -> Unit)? =
        { content ->
            if (error != null) {
                content
            } else {
                null
            }
        }

    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
        OutlinedTextField(
            value = options.firstOrNull { it.selected }?.label ?: "",
            onValueChange = {},
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth(),
            readOnly = true,
            label = { Text(text = label) },
            trailingIcon = {
                Icon(
                    imageVector = if (expanded) {
                        Icons.Filled.ArrowDropUp
                    } else {
                        Icons.Filled.ArrowDropDown
                    },
                    contentDescription = "",
                    tint = CustomColorScheme.icon()
                )
            },
            isError = error != null,
            supportingText = resolveSupportText {
                Text(text = error!!)
            },
            colors = CustomColorScheme.outlineTextField()
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .background(CustomColorScheme.dropDownMenu())
                .exposedDropdownSize()
        ) {
            options.forEach {
                DropdownMenuItem(
                    text = { Text(text = it.label) },
                    onClick = {
                        onOptionSelected(it)
                        expanded = false
                    })
            }
        }
    }

}