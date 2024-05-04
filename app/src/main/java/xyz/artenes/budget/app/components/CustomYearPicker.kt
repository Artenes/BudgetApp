package xyz.artenes.budget.app.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import xyz.artenes.budget.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomYearPicker(
    visible: Boolean,
    value: Int,
    onYearSelected: (Int) -> Unit,
    onDismiss: () -> Unit,
) {

    val coroutine = rememberCoroutineScope()

    var years by remember {
        mutableStateOf(makeYears(value))
    }

    val yearListState = rememberLazyGridState(
        initialFirstVisibleItemIndex = years.first { it.selected }.position
    )

    if (visible) {
        BasicAlertDialog(onDismissRequest = {
            years = makeYears(value)
            coroutine.launch {
                yearListState.scrollToItem(years.first { it.selected }.position)
            }
            onDismiss()
        }) {
            Surface(
                color = DatePickerDefaults.colors().containerColor,
                shape = DatePickerDefaults.shape,
                modifier = Modifier
                    .heightIn(max = 580.dp)
                    .requiredWidth(360.dp)
            ) {

                Column(
                    modifier = Modifier.padding(30.dp)
                ) {

                    /*
                    Years
                     */
                    DateRow(state = yearListState, items = years) { yearItem ->
                        years =
                            years.map { item -> item.copy(selected = yearItem == item) }.toList()
                    }

                    Button(
                        onClick = {
                            val year = years.first { it.selected }.value
                            onYearSelected(year)
                            onDismiss()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 20.dp),
                        enabled = years.find { it.selected } != null
                    ) {
                        Text(text = stringResource(R.string.select_year))
                    }

                }

            }
        }
    }

}

private fun makeYears(value: Int): List<DateItem> {
    val start = value - 7
    val end = value + 7
    return (start..end).mapIndexed { index, item ->
        DateItem(item, item == value, item.toString(), index)
    }.toList()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DateRow(
    state: LazyGridState,
    items: List<DateItem>,
    onClick: (DateItem) -> Unit
) {

    LazyVerticalGrid(
        state = state,
        columns = GridCells.Fixed(3)
    ) {

        items(
            count = items.size,
            key = { index -> items[index].value }
        ) { index ->

            val dateItem = items[index]

            Surface(
                color = if (dateItem.selected) MaterialTheme.colorScheme.primary else DatePickerDefaults.colors().containerColor,
                onClick = {
                    onClick(dateItem)
                },
                shape = MaterialTheme.shapes.medium
            ) {
                Text(
                    modifier = Modifier.padding(10.dp),
                    text = dateItem.label,
                    textAlign = TextAlign.Center
                )
            }

        }

    }
}