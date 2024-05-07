package xyz.artenes.budget.app.transaction.search

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import xyz.artenes.budget.R
import xyz.artenes.budget.app.components.CustomAdvancedDatePicker
import xyz.artenes.budget.app.components.CustomSpinner
import xyz.artenes.budget.app.components.Transaction
import xyz.artenes.budget.app.theme.CustomColorScheme
import xyz.artenes.budget.data.SearchResultsData
import xyz.artenes.budget.utils.DataState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    back: () -> Unit,
    viewModel: SearchViewModel = hiltViewModel()
) {

    val transactionsDataState by viewModel.transactions.collectAsState()
    val dateFilter by viewModel.dateFilter.collectAsState()
    val types by viewModel.types.collectAsState()
    val categories by viewModel.categories.collectAsState()
    val sorts by viewModel.sorts.collectAsState()
    val scrollState = rememberLazyListState()
    var showFilters by remember {
        mutableStateOf(true)
    }
    val totalsPadding by animateDpAsState(
        targetValue = if (showFilters) 30.dp else 5.dp,
        label = "totalsPadding"
    )

    LaunchedEffect(transactionsDataState) {
        scrollState.scrollToItem(0)
    }

    LaunchedEffect(scrollState) {
        snapshotFlow { scrollState.firstVisibleItemIndex }.collect { firstVisibleItemIndex ->
            //showFilters = firstVisibleItemIndex <= 0
        }
    }

    Scaffold(
        topBar = {
            AnimatedVisibility(visible = showFilters) {
                TopAppBar(
                    colors = TopAppBarDefaults.topAppBarColors()
                        .copy(containerColor = MaterialTheme.colorScheme.tertiaryContainer),
                    title = {
                        OutlinedTextField(
                            value = "",
                            onValueChange = {},
                            colors = CustomColorScheme.outlineTextField()
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = back) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = MaterialTheme.colorScheme.onBackground
                            )
                        }
                    }
                )
            }
        }
    ) { paddings ->

        Column(
            modifier = Modifier
                .padding(paddings)
        ) {

            //general container
            Column(
                modifier = Modifier.background(MaterialTheme.colorScheme.tertiaryContainer)
            ) {

                //filters
                AnimatedVisibility(visible = showFilters) {
                    Column(
                        modifier = Modifier.padding(horizontal = 20.dp)
                    ) {

                        CustomAdvancedDatePicker(
                            value = dateFilter,
                            onChange = { newFilter -> viewModel.setDateFilter(newFilter) }
                        )

                        CustomSpinner(
                            label = stringResource(id = R.string.filter_by_type),
                            options = types,
                            onOptionSelected = { newType ->
                                viewModel.setType(newType)
                            }
                        )

                        CustomSpinner(
                            label = stringResource(id = R.string.filter_by_category),
                            options = categories,
                            onOptionSelected = { newCategory ->
                                viewModel.setCategory(newCategory)
                            }
                        )

                        CustomSpinner(
                            label = stringResource(id = R.string.sort_by),
                            options = sorts,
                            onOptionSelected = { newSort ->
                                viewModel.setSort(newSort)
                            }
                        )
                    }
                }

                //totals
                Row(modifier = Modifier.padding(top = totalsPadding)) {

                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            modifier = Modifier.fillMaxWidth(),
                            text = stringResource(id = R.string.income),
                            color = CustomColorScheme.textColor(),
                            style = MaterialTheme.typography.labelSmall,
                            textAlign = TextAlign.Center
                        )
                        TotalText(
                            dataState = transactionsDataState,
                            value = { resultsData ->
                                resultsData.formattedTotalIncome
                            },
                            opacity = {
                                1.0f
                            },
                        )
                    }

                    Column(
                        modifier = Modifier.weight(1f),
                    ) {
                        Text(
                            modifier = Modifier.fillMaxWidth(),
                            text = stringResource(R.string.expenses),
                            color = CustomColorScheme.textColor(),
                            style = MaterialTheme.typography.labelSmall,
                            textAlign = TextAlign.Center
                        )
                        TotalText(
                            dataState = transactionsDataState,
                            value = { resultsData ->
                                resultsData.formattedTotalExpenses
                            },
                            opacity = {
                                0.8f
                            },
                        )
                    }

                }

                //balance
                AnimatedVisibility(visible = showFilters) {
                    Column {
                        Text(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 20.dp),
                            text = "Balance",
                            color = CustomColorScheme.textColor(),
                            style = MaterialTheme.typography.labelSmall,
                            textAlign = TextAlign.Center
                        )

                        TotalText(
                            dataState = transactionsDataState,
                            value = { resultsData ->
                                resultsData.formattedBalance
                            },
                            opacity = { resultsData ->
                                resultsData.balanceOpacity
                            },
                        )

                        TransactionsAmount(
                            dataState = transactionsDataState,
                            padding = totalsPadding
                        )
                    }

                }

            }

            TransactionList(dataState = transactionsDataState, state = scrollState)

        } //container

    }

}

@Composable
private fun TotalText(
    dataState: DataState<SearchResultsData>,
    value: (SearchResultsData) -> String,
    opacity: (SearchResultsData) -> Float,
) {

    if (dataState !is DataState.Success) {
        Loading(modifier = Modifier.fillMaxWidth(), size = 15.dp)
        return
    }

    Text(
        modifier = Modifier.fillMaxWidth(),
        text = value(dataState.data),
        color = CustomColorScheme.textColor().copy(alpha = opacity(dataState.data)),
        style = MaterialTheme.typography.titleMedium,
        textAlign = TextAlign.Center
    )

}

@Composable
private fun TransactionsAmount(
    dataState: DataState<SearchResultsData>,
    padding: Dp
) {

    Box(modifier = Modifier.padding(top = padding)) {

        if (dataState !is DataState.Success) {
            Loading(modifier = Modifier.fillMaxWidth(), size = 15.dp)
            return
        }

        Text(
            modifier = Modifier.fillMaxWidth(),
            text = stringResource(R.string.transactions_count, dataState.data.totalTransactions),
            color = CustomColorScheme.textColor(),
            style = MaterialTheme.typography.labelSmall,
            textAlign = TextAlign.Center
        )

    }

}

@Composable
private fun TransactionList(
    dataState: DataState<SearchResultsData>,
    state: LazyListState
) {

    if (dataState !is DataState.Success) {
        Loading(modifier = Modifier.fillMaxSize())
        return
    }

    val transactions = dataState.data.transactions

    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
        state = state
    ) {

        items(
            count = transactions.size,
            key = { index -> transactions[index].id }
        ) { index ->

            Transaction(
                transaction = transactions[index],
                showDate = true,
            )

        }

    }

}

@Composable
private fun Loading(modifier: Modifier = Modifier, size: Dp = 40.dp) {

    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        CircularProgressIndicator(
            modifier = Modifier.size(size),
            color = MaterialTheme.colorScheme.onBackground,
        )
    }

}