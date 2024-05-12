package xyz.artenes.budget.app.category.editor

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import xyz.artenes.budget.R
import xyz.artenes.budget.app.components.ConfirmDialog
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
    val icon by viewModel.icon.collectAsState()
    val event by viewModel.event.collectAsState()
    val categories by viewModel.categories.collectAsState()
    val isLastCategory by viewModel.lastCategory.collectAsState()

    var showInfo by remember {
        mutableStateOf(false)
    }

    var showDeleteDialog by remember {
        mutableStateOf(false)
    }

    var showConfirmDeleteDialog by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(key1 = event) {
        event.consume {
            when (it) {
                CategoryEditorActions.FINISH -> onBack()
                CategoryEditorActions.REASSIGN_CATEGORY -> {
                    showConfirmDeleteDialog = true
                }

                CategoryEditorActions.CONFIRM_DELETE -> {
                    showDeleteDialog = true
                }
            }
        }
    }

    ConfirmDialog(
        show = showDeleteDialog,
        title = stringResource(R.string.delete_category),
        body = stringResource(R.string.this_category_will_be_deleted_and_can_t_be_restored),
        onDismiss = { showDeleteDialog = false },
        onConfirm = viewModel::delete,
    )

    ConfirmDialog(
        show = showInfo,
        title = stringResource(R.string.you_can_t_delete_this_category),
        body = stringResource(R.string.create_new_categories),
        onDismiss = { showInfo = false },
        confirmText = stringResource(R.string.ok),
    )

    DeleteCategoryDialog(
        show = showConfirmDeleteDialog,
        categories = categories,
        onDismiss = { showConfirmDeleteDialog = false },
        onConfirm = viewModel::deleteAndReassign
    )

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
                        IconButton(onClick = {
                            if (!isLastCategory) {
                                viewModel.requestDelete()
                            } else {
                                showInfo = true
                            }
                        }) {
                            Icon(
                                imageVector = Icons.Filled.Delete,
                                contentDescription = ""
                            )
                        }
                    }
                }
            )

        },
        floatingActionButton = {

            FloatingActionButton(onClick = { viewModel.save() }) {
                Icon(imageVector = Icons.Filled.Check, contentDescription = "")
            }

        }
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
                modifier = Modifier.padding(top = 20.dp),
                label = stringResource(R.string.icon),
                value = icon.value,
                onIconSelected = viewModel::setIcon
            )

        }

    }

}