package xyz.artenes.budget.app.category.editor

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import xyz.artenes.budget.R
import xyz.artenes.budget.app.theme.CustomColorScheme
import xyz.artenes.budget.data.models.CategoryEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeleteCategoryDialog(
    show: Boolean,
    categories: List<CategoryEntity>,
    onDismiss: () -> Unit,
    onConfirm: (CategoryEntity) -> Unit
) {

    if (!show) {
        return
    }

    BasicAlertDialog(onDismissRequest = onDismiss) {

        Surface(
            color = DatePickerDefaults.colors().containerColor,
            shape = DatePickerDefaults.shape,
            modifier = Modifier
                .heightIn(max = 580.dp)
                .requiredWidth(360.dp)
        ) {

            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                Text(
                    text = stringResource(R.string.select_a_new_category),
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    modifier = Modifier.padding(top = 10.dp),
                    text = stringResource(R.string.before_deleting_category),
                    style = MaterialTheme.typography.bodyMedium
                )
                LazyColumn {
                    items(
                        count = categories.size,
                        key = { index -> categories[index].id }
                    ) { index ->

                        Surface(
                            onClick = { onConfirm(categories[index]) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 10.dp)
                        ) {

                            Box(
                                modifier = Modifier
                                    .border(
                                        1.dp,
                                        CustomColorScheme.border(),
                                        MaterialTheme.shapes.small
                                    )
                                    .fillMaxWidth()
                            ) {

                                Text(
                                    text = categories[index].name,
                                    modifier = Modifier.padding(10.dp)
                                )

                            }

                        }

                    }
                }
            }

        }

    }

}