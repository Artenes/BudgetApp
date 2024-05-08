package xyz.artenes.budget.app.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import xyz.artenes.budget.app.theme.CustomColorScheme

@Composable
fun MoneyTextField(
    modifier: Modifier = Modifier,
    label: String = "",
    value: String = "",
    errorMessage: String? = null,
    onValueChange: (String) -> Unit = {},
    imeAction: ImeAction = ImeAction.Done,
    keyboardActions: KeyboardActions = KeyboardActions(),
) {

    OutlinedTextField(
        modifier = Modifier
            .fillMaxWidth()
            .then(modifier),
        label = {
            Text(text = label)
        },
        isError = errorMessage != null,
        supportingText = if (errorMessage != null) {
            { Text(text = errorMessage) }
        } else {
            null
        },
        value = TextFieldValue(text = value, selection = TextRange(value.length)),
        onValueChange = { textFieldValue -> onValueChange(textFieldValue.text) },
        colors = CustomColorScheme.outlineTextField(),
        maxLines = 1,
        keyboardOptions = KeyboardOptions(
            imeAction = imeAction,
            keyboardType = KeyboardType.Number
        ),
        keyboardActions = keyboardActions
    )

}