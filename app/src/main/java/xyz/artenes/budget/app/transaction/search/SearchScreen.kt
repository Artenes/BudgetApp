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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CalendarMonth
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import xyz.artenes.budget.app.components.CustomAdvancedDatePicker
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
    val scrollState = rememberLazyListState()
    var showFilters by remember {
        mutableStateOf(true)
    }
    val totalsPadding by animateDpAsState(
        targetValue = if (showFilters) 30.dp else 5.dp,
        label = "totalsPadding"
    )

    LaunchedEffect(scrollState) {
        snapshotFlow { scrollState.firstVisibleItemIndex }.collect { firstVisibleItemIndex ->
            showFilters = firstVisibleItemIndex <= 0
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
    ) {

        Column(
            modifier = Modifier
                .padding(it)
        ) {

            //geral container
            Column(
                modifier = Modifier.background(MaterialTheme.colorScheme.tertiaryContainer)
            ) {

                //filters
                AnimatedVisibility(visible = showFilters) {
                    Column(
                        modifier = Modifier.padding(horizontal = 20.dp)
                    ) {


                        CustomAdvancedDatePicker()

                        OutlinedTextField(
                            value = "",
                            onValueChange = { },
                            modifier = Modifier.fillMaxWidth(),
                            colors = CustomColorScheme.outlineTextField(),
                            label = {
                                Text("Filter by type")
                            },
                            trailingIcon = {
                                Icon(
                                    imageVector = Icons.Filled.ArrowDropDown,
                                    contentDescription = "",
                                    tint = MaterialTheme.colorScheme.onBackground
                                )
                            },
                            readOnly = true
                        )

                        OutlinedTextField(
                            value = "",
                            onValueChange = { },
                            modifier = Modifier.fillMaxWidth(),
                            colors = CustomColorScheme.outlineTextField(),
                            label = {
                                Text("Sort by")
                            },
                            trailingIcon = {
                                Icon(
                                    imageVector = Icons.Filled.ArrowDropDown,
                                    contentDescription = "",
                                    tint = MaterialTheme.colorScheme.onBackground
                                )
                            },
                            readOnly = true
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
                            text = "Income",
                            color = CustomColorScheme.textColor(),
                            style = MaterialTheme.typography.labelSmall,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            modifier = Modifier.fillMaxWidth(),
                            text = "+ $ 100.000,00",
                            color = CustomColorScheme.textColor(),
                            style = MaterialTheme.typography.titleMedium,
                            textAlign = TextAlign.Center
                        )
                    }

                    Column(
                        modifier = Modifier.weight(1f),
                    ) {
                        Text(
                            modifier = Modifier.fillMaxWidth(),
                            text = "Expenses",
                            color = CustomColorScheme.textColor(),
                            style = MaterialTheme.typography.labelSmall,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            modifier = Modifier.fillMaxWidth(),
                            text = "+ $ 100.000,00",
                            color = CustomColorScheme.textColor(),
                            style = MaterialTheme.typography.titleMedium,
                            textAlign = TextAlign.Center
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

                        Text(
                            modifier = Modifier.fillMaxWidth(),
                            text = "+ $ 100.000,00",
                            color = CustomColorScheme.textColor(),
                            style = MaterialTheme.typography.titleMedium,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = totalsPadding),
                    text = "340 transactions",
                    color = CustomColorScheme.textColor(),
                    style = MaterialTheme.typography.labelSmall,
                    textAlign = TextAlign.Center
                )
            }

            TransactionList(dataState = transactionsDataState, state = scrollState)

        } //container

    }

}

@Composable
private fun TransactionList(
    dataState: DataState<SearchResultsData>,
    state: LazyListState
) {

    if (dataState !is DataState.Success) {
        Loading()
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

            Transaction(transaction = transactions[index])

        }

    }

}

@Composable
private fun Loading() {

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator(
            color = MaterialTheme.colorScheme.onBackground
        )
    }

}