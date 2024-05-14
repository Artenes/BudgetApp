package xyz.artenes.budget.app.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import xyz.artenes.budget.app.theme.CustomColorScheme

@Composable
fun CustomTextField(
    modifier: Modifier = Modifier,
    label: String = "",
    value: String = "",
    errorMessage: String? = null,
    onValueChange: (String) -> Unit = {},
    imeAction: ImeAction = ImeAction.Done,
    keyboardActions: KeyboardActions = KeyboardActions(),
    keyboardType: KeyboardType = KeyboardType.Text
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
            {
                Text(
                    text = errorMessage,
                    color = CustomColorScheme.textError()
                )
            }
        } else {
            null
        },
        value = value,
        onValueChange = onValueChange,
        colors = CustomColorScheme.outlineTextField(),
        maxLines = 1,
        keyboardOptions = KeyboardOptions(
            imeAction = imeAction,
            keyboardType = keyboardType
        ),
        keyboardActions = keyboardActions
    )

}