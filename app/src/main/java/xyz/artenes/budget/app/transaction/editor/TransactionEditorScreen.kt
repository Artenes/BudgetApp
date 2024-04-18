package xyz.artenes.budget.app.transaction.editor

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
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

        Column(
            modifier = Modifier
                .padding(it)
                .padding(20.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            val description by viewModel.description.collectAsState()
            val amount by viewModel.amount.collectAsState()

            Text(
                modifier = Modifier.padding(bottom = 20.dp),
                text = "Create Transaction",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onBackground
            )

            TextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 10.dp),
                label = {
                    Text(text = "Description")
                },
                value = description,
                onValueChange = { value ->
                    viewModel.setDescription(value)
                },
                colors = TextFieldDefaults.colors().copy(
                    unfocusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                    focusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                    focusedTextColor = MaterialTheme.colorScheme.onSecondaryContainer,
                )
            )

            TextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 10.dp),
                label = {
                    Text(text = "Price")
                },
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                value = amount,
                onValueChange = { value ->
                    viewModel.setAmount(value)
                },
                colors = TextFieldDefaults.colors().copy(
                    unfocusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                    focusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                    focusedTextColor = MaterialTheme.colorScheme.onSecondaryContainer,
                )
            )

            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = { viewModel.save() },
                shape = MaterialTheme.shapes.small,
                colors = ButtonDefaults.buttonColors().copy(
                    containerColor = MaterialTheme.colorScheme.tertiary,
                )
            ) {
                Text(
                    color = MaterialTheme.colorScheme.onTertiary,
                    text = "Save"
                )
            }

        }

    }

}