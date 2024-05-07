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
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import xyz.artenes.budget.R
import xyz.artenes.budget.app.components.CustomDatePickerInput
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
            val date by viewModel.date.collectAsState()
            val categories by viewModel.categories.collectAsState()
            val types by viewModel.types.collectAsState()

            Spacer(modifier = Modifier.height(80.dp))

            //TODO make text normal case and translated
            //type
            CustomSpinner(
                label = stringResource(R.string.type),
                options = types,
                onOptionSelected = { item ->
                    viewModel.setType(item.value)
                }
            )

            Spacer(modifier = Modifier.height(10.dp))

            //description
            CustomTextField(
                modifier = Modifier.padding(bottom = 10.dp),
                label = stringResource(R.string.description),
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

            //TODO add money mask
            //amount
            CustomTextField(
                modifier = Modifier.padding(bottom = 10.dp),
                label = stringResource(R.string.amount),
                value = amount.value,
                errorMessage = amount.error,
                onValueChange = viewModel::setAmount,
                keyboardType = KeyboardType.Number
            )

            //date
            CustomDatePickerInput(
                label = stringResource(R.string.date),
                value = date,
                onDateSelected = { newDate ->
                    viewModel.setDate(newDate)
                }
            )

            Spacer(modifier = Modifier.height(10.dp))

            //category
            CustomSpinner(
                label = stringResource(R.string.category),
                options = categories,
                onOptionSelected = { item ->
                    viewModel.setCategory(item)
                }
            )

        }

    }

}