package xyz.artenes.budget.app.transaction.list

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.InputChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import xyz.artenes.budget.app.components.CustomDatePicker
import xyz.artenes.budget.app.components.CustomWeekPicker
import xyz.artenes.budget.app.theme.CustomColorScheme
import xyz.artenes.budget.utils.DataState
import xyz.artenes.budget.utils.LocalDateRange
import java.time.LocalDate

@Composable
fun TransactionsListScreen(
    navigateToTransactionEditScreen: () -> Unit,
    viewModel: TransactionsListViewModel = hiltViewModel()
) {

    val loading by viewModel.loading.collectAsState()

    Scaffold(
        floatingActionButton = {
            if (!loading) {
                FloatingActionButton(onClick = navigateToTransactionEditScreen) {
                    Icon(imageVector = Icons.Filled.Add, contentDescription = "")
                }
            }
        }

    ) {

        if (loading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
            return@Scaffold
        }

        val transactionsDataState by viewModel.transactionsData.collectAsState()
        val filtersState by viewModel.filters.collectAsState()
        val filterValueState by viewModel.filterValue.collectAsState()
        val query by viewModel.query.collectAsState()

        val transactionsData = (transactionsDataState as DataState.Success).data
        val filters = (filtersState as DataState.Success).data
        val filterValue = (filterValueState as DataState.Success).data

        val scrollState = rememberLazyListState()
        var showFilter by remember {
            mutableStateOf(true)
        }
        val totalsPadding by animateDpAsState(
            targetValue = if (showFilter) 50.dp else 10.dp,
            label = "totalsPadding"
        )

        LaunchedEffect(scrollState) {
            snapshotFlow { scrollState.firstVisibleItemIndex }.collect { firstVisibleItemIndex ->
                showFilter = firstVisibleItemIndex <= 3
            }
        }

        Column(
            modifier = Modifier.padding(it)
        ) {


            Column(
                Modifier.background(MaterialTheme.colorScheme.tertiaryContainer)
            ) {

                AnimatedVisibility(visible = showFilter) {

                    Column {
                        SearchBox(
                            value = query,
                            onValueChange = { newValue ->
                                viewModel.search(newValue)
                            },
                            onClearClick = {
                                viewModel.search("")
                            }
                        )

                        Filters(
                            viewModel = viewModel,
                            filters = filters
                        )

                        SelectedFilter(
                            viewModel = viewModel,
                            filterValue = filterValue
                        )
                    }

                }

                Totals(
                    padding = totalsPadding,
                    income = transactionsData.formattedTotalIncome,
                    expense = transactionsData.formattedTotalExpenses
                )

                AmountOfTransaction(value = transactionsData.totalTransactions)

            }

            Transactions(
                scrollState = scrollState,
                groups = transactionsData.groups
            )

        }

    }

}

@Composable
private fun Transactions(
    scrollState: LazyListState,
    groups: List<TransactionGroupItem>
) {
    LazyColumn(
        state = scrollState
    ) {

        item {
            Box(modifier = Modifier.height(20.dp))
        }

        groups.forEach { group ->

            /*
            Date
             */
            item {

                Row(
                    modifier = Modifier.padding(horizontal = 20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        modifier = Modifier.padding(end = 10.dp),
                        color = MaterialTheme.colorScheme.onBackground,
                        text = group.date.displayValue,
                        style = MaterialTheme.typography.bodyLarge
                    )
                    if (group.date.isRelative) {
                        Text(
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                            text = group.date.absolute,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

                }
            }

            items(
                count = group.transactions.size,
                key = { index ->
                    group.transactions[index].id
                }
            ) { index ->

                Transaction(
                    transaction = group.transactions[index]
                )

            }

            item {
                Spacer(modifier = Modifier.height(50.dp))
            }

        }

        item {

            Spacer(modifier = Modifier.height(100.dp))

        }

    }
}

@Composable
private fun AmountOfTransaction(value: Int) {
    Text(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 5.dp),
        textAlign = TextAlign.Center,
        text = "$value transactions",
        color = CustomColorScheme.textColor(),
        textDecoration = TextDecoration.Underline,
        style = MaterialTheme.typography.labelSmall
    )
}

@Composable
private fun Totals(
    padding: Dp,
    income: String,
    expense: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = padding),
    ) {

        Row(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Filled.ArrowDownward,
                contentDescription = "",
                tint = CustomColorScheme.textColor()
            )
            Text(
                text = "+ $income",
                color = CustomColorScheme.textColor(),
                style = MaterialTheme.typography.titleLarge
            )
        }

        Row(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Filled.ArrowUpward,
                contentDescription = "",
                tint = CustomColorScheme.textColor().copy(alpha = 0.7f)
            )
            Text(
                text = "- $expense",
                color = CustomColorScheme.textColor().copy(alpha = 0.7f),
                style = MaterialTheme.typography.titleLarge
            )
        }

    }
}

@Composable
private fun SelectedFilter(filterValue: DateFilterValueItem, viewModel: TransactionsListViewModel) {

    var show by remember {
        mutableStateOf(false)
    }

    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {

        InputChip(
            selected = false,
            onClick = { show = true },
            label = { Text(text = filterValue.label) },
            trailingIcon = {
                Icon(imageVector = Icons.Filled.Edit, contentDescription = "")
            },
            colors = CustomColorScheme.inputChipColor(),
            border = CustomColorScheme.chipBorder()
        )

    }

    if (filterValue.type == DateFilterType.DAY) {
        CustomDatePicker(
            visible = show,
            value = filterValue.value as LocalDate,
            onDateSelected = { newDate ->
                viewModel.setValueForDay(
                    newDate
                )
            },
            onDismiss = { show = false }
        )
    }

    if (filterValue.type == DateFilterType.WEEK) {
        CustomWeekPicker(
            visible = show,
            value = filterValue.value as LocalDateRange,
            onWeekSelected = { newWeek ->
                viewModel.setValueForWeek(newWeek)
            },
            onDismiss = { show = false }
        )
    }

}

@Composable
private fun Filters(
    viewModel: TransactionsListViewModel,
    filters: List<DateFilterItem>
) {
    LazyRow {

        items(
            count = filters.size,
            key = { index -> filters[index].type }
        ) { index ->

            val filter = filters[index]

            val startPadding = if (index == 0) 20.dp else 0.dp

            FilterChip(
                modifier = Modifier.padding(end = 10.dp, start = startPadding),
                selected = filter.selected,
                onClick = { viewModel.setFilterType(filter) },
                label = { Text(text = filter.label) },
                colors = CustomColorScheme.filterChipColor(),
                border = CustomColorScheme.chipBorder(
                    enabled = true,
                    selected = filter.selected
                )
            )

        }

    }
}

@Composable
private fun SearchBox(value: String, onValueChange: (String) -> Unit, onClearClick: () -> Unit) {
    OutlinedTextField(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 20.dp, end = 20.dp, top = 20.dp),
        value = value,
        onValueChange = onValueChange,
        colors = CustomColorScheme.outlineTextField(),
        placeholder = { Text(text = "Search transactions") },
        trailingIcon = {
            if (value.isNotEmpty()) {
                IconButton(onClick = onClearClick) {
                    Icon(imageVector = Icons.Filled.Close, contentDescription = "")
                }
            }
        }
    )
}

@Composable
private fun Transaction(
    transaction: TransactionItem
) {

    Surface(
        color = Color.Transparent,
        onClick = { }
    ) {

        Column {

            Row(
                modifier = Modifier.padding(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .border(
                            1.dp,
                            MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = transaction.icon,
                        contentDescription = "",
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Text(
                    color = MaterialTheme.colorScheme.onBackground,
                    text = transaction.description,
                    style = MaterialTheme.typography.titleLarge
                )

                Spacer(modifier = Modifier.weight(1f))

                /*
                Amount
                 */
                Text(
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = transaction.colorAlpha),
                    text = transaction.formattedAmount,
                    style = MaterialTheme.typography.titleLarge
                )

            }

            HorizontalDivider(
                thickness = 2.dp,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.2f)
            )

        }

    }

}