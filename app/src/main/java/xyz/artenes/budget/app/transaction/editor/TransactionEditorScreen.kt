package xyz.artenes.budget.app.transaction.editor

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun TransactionEditorScreen(
    back: () -> Unit,
    viewModel: TransactionEditorViewModel = hiltViewModel()
) {

    Scaffold { it ->

        val event by viewModel.event.collectAsState()
        LaunchedEffect(key1 = event) {
            event.consume {
                back()
            }
        }

        Column(modifier = Modifier.padding(it)) {

            val description by viewModel.description.collectAsState()
            val amount by viewModel.amount.collectAsState()

            TextField(
                label = {
                    Text(text = "Description")
                },
                value = description,
                onValueChange = { value ->
                    viewModel.setDescription(value)
                }
            )

            TextField(
                label = {
                    Text(text = "Price")
                },
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                value = amount,
                onValueChange = { value ->
                    viewModel.setAmount(value)
                }
            )
            OutlinedButton(onClick = { viewModel.save() }) {
                Text(text = "Save")
            }

        }

    }

}