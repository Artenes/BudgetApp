package xyz.artenes.budget.app.category

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import xyz.artenes.budget.R
import xyz.artenes.budget.app.components.CustomIconPickerInput
import xyz.artenes.budget.app.components.CustomSpinner
import xyz.artenes.budget.app.components.CustomTextField
import xyz.artenes.budget.di.FactoryLocator
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryEditorScreen(
    id: UUID? = null,
    onBack: () -> Unit,
    viewModel: CategoryEditorViewModel = viewModel(
        factory = FactoryLocator.instance.categoryEditorFactory.make(
            id
        )
    )
) {

    val focusManager = LocalFocusManager.current
    val name by viewModel.name.collectAsState()
    val types by viewModel.types.collectAsState()

    Scaffold(
        topBar = {

            TopAppBar(
                title = { },
                colors = TopAppBarDefaults.topAppBarColors().copy(
                    containerColor = MaterialTheme.colorScheme.background
                ),
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = ""
                        )
                    }
                },
                actions = {
                    if (id != null) {
                        IconButton(onClick = { }) {
                            Icon(
                                imageVector = Icons.Filled.Delete,
                                contentDescription = ""
                            )
                        }
                    }
                }
            )

        },
    ) { screen ->

        Column(modifier = Modifier.padding(screen)) {

            CustomTextField(
                modifier = Modifier.padding(bottom = 10.dp),
                label = stringResource(R.string.name),
                value = name.value,
                onValueChange = viewModel::setName,
                errorMessage = name.error,
                imeAction = ImeAction.Next,
                keyboardActions = KeyboardActions(
                    onNext = {
                        focusManager.moveFocus(FocusDirection.Down)
                    },
                )
            )

            CustomSpinner(
                label = stringResource(R.string.type),
                options = types,
                onOptionSelected = { item ->
                    viewModel.setType(item.value)
                }
            )

            CustomIconPickerInput(
                label = stringResource(R.string.icon),
                value = Icons.Filled.FitnessCenter,
                onIconSelected = { icon -> })

        }

    }

}