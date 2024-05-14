package xyz.artenes.budget.app.transaction.list

import android.app.Activity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import xyz.artenes.budget.app.transaction.Transaction
import xyz.artenes.budget.app.theme.CustomColorScheme
import xyz.artenes.budget.core.models.TransactionsData
import xyz.artenes.budget.core.models.DataState
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionsListScreen(
    navigateToTransactionEditScreen: () -> Unit,
    navigateToSearchScreen: () -> Unit,
    navigateToTransaction: (UUID) -> Unit,
    navigateToCategories: () -> Unit,
    viewModel: TransactionsListViewModel = hiltViewModel()
) {

    val view = LocalView.current
    val statusBarColor = CustomColorScheme.statusBar()
    SideEffect {
        (view.context as Activity).window.statusBarColor = statusBarColor.toArgb()
    }

    val transactionsDataState by viewModel.transactionsData.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                colors = CustomColorScheme.topAppBar(),
                title = { },
                actions = {
                    if (transactionsDataState is DataState.Success) {
                        IconButton(onClick = navigateToCategories) {
                            Icon(
                                imageVector = Icons.Filled.Checklist,
                                contentDescription = "",
                                tint = CustomColorScheme.icon()
                            )
                        }
                        IconButton(onClick = navigateToSearchScreen) {
                            Icon(
                                imageVector = Icons.Filled.Search,
                                contentDescription = "Search",
                                tint = CustomColorScheme.icon()
                            )
                        }
                    }
                },
            )
        },
        floatingActionButton = {
            if (transactionsDataState is DataState.Success) {
                FloatingActionButton(onClick = navigateToTransactionEditScreen) {
                    Icon(imageVector = Icons.Filled.Add, contentDescription = "")
                }
            }
        }

    ) {

        if (transactionsDataState is DataState.Loading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(
                    color = CustomColorScheme.icon()
                )
            }
            return@Scaffold
        }

        val transactionsData = (transactionsDataState as DataState.Success).data

        if (transactionsData.groups.isEmpty()) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = "No transactions",
                    color = CustomColorScheme.textColor(),
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(bottom = 10.dp)
                )
                Text(
                    text = "Tap the '+' button to add your first expense",
                    color = CustomColorScheme.textColor(),
                    style = MaterialTheme.typography.bodySmall
                )
            }
            return@Scaffold
        }

        Column(
            modifier = Modifier.padding(it)
        ) {

            Transactions(
                data = transactionsData,
                navigateToTransaction = navigateToTransaction
            )

        }

    }

}

@Composable
private fun Transactions(
    data: TransactionsData,
    navigateToTransaction: (UUID) -> Unit,
) {
    LazyColumn {

        item {

            Column(
                modifier = Modifier.padding(start = 20.dp, top = 120.dp, bottom = 120.dp)
            ) {

                Text(
                    text = "You've spent this month (${data.formattedCurrentMonth})",
                    color = CustomColorScheme.textColor(),
                    style = MaterialTheme.typography.labelSmall
                )
                Text(
                    text = data.formattedTotalExpenses,
                    color = CustomColorScheme.textColor().copy(alpha = 0.7f),
                    style = MaterialTheme.typography.headlineLarge.copy(fontSize = 50.sp),
                    modifier = Modifier.padding(bottom = 30.dp)
                )
                Text(
                    text = "Earned: ${data.formattedTotalIncome}",
                    color = CustomColorScheme.textColor().copy(alpha = 0.9f),
                    style = MaterialTheme.typography.titleSmall,
                    modifier = Modifier.padding(bottom = 10.dp)
                )
                Text(
                    text = "Balance: ${data.formattedBalance}",
                    color = CustomColorScheme.textColor().copy(alpha = 0.6f),
                    style = MaterialTheme.typography.titleSmall
                )

            }

        }

        data.groups.forEach { group ->

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
                        color = CustomColorScheme.textColor(),
                        text = group.date.displayValue,
                        style = MaterialTheme.typography.bodyLarge
                    )
                    if (group.date.isRelative) {
                        Text(
                            color = CustomColorScheme.textColorLight(),
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
                    transaction = group.transactions[index],
                    onClicked = { item -> navigateToTransaction(item.id) }
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