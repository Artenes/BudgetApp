package xyz.artenes.budget.app.transaction.editor

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import xyz.artenes.budget.app.components.CustomSpinner
import xyz.artenes.budget.app.components.CustomTextField

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionEditorScreen(
    back: () -> Unit,
    viewModel: TransactionEditorViewModel = hiltViewModel()
) {

    Scaffold(

        topBar = {

            TopAppBar(
                title = { },
                colors = TopAppBarDefaults.topAppBarColors().copy(
                    containerColor = MaterialTheme.colorScheme.background
                ),
                navigationIcon = {
                    IconButton(onClick = back) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = ""
                        )
                    }
                }
            )

        },

        floatingActionButton = {

            FloatingActionButton(onClick = { viewModel.save() }) {
                Icon(imageVector = Icons.Filled.Check, contentDescription = "")
            }

        }

    ) { padding ->

        val event by viewModel.event.collectAsState()
        LaunchedEffect(key1 = event) {
            event.consume {
                back()
            }
        }

        Column(
            modifier = Modifier
                .padding(padding)
                .padding(20.dp)
                .fillMaxSize(),
        ) {

            /*
            State
             */
            val focusManager = LocalFocusManager.current
            val description by viewModel.description.collectAsState()
            val amount by viewModel.amount.collectAsState()

            Spacer(modifier = Modifier.height(120.dp))

            var value by remember {
                mutableStateOf("expense")
            }
            CustomSpinner(
                label = "Type",
                value = value,
                options = listOf("expense", "income"),
                onOptionSelected = {
                    value = it
                }
            )

            Spacer(modifier = Modifier.height(10.dp))

            CustomTextField(
                modifier = Modifier.padding(bottom = 10.dp),
                label = "Description",
                value = description.value,
                onValueChange = viewModel::setDescription,
                errorMessage = description.error,
                imeAction = ImeAction.Next,
                keyboardActions = KeyboardActions(
                    onNext = {
                        focusManager.moveFocus(FocusDirection.Down)
                    },
                )
            )

            CustomTextField(
                modifier = Modifier.padding(bottom = 10.dp),
                label = "Amount",
                value = amount.value,
                errorMessage = amount.error,
                onValueChange = viewModel::setAmount,
                keyboardType = KeyboardType.Number
            )

        }

    }

}