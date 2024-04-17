package xyz.artenes.budgetapp.ui.transactions

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun BudgetScreen(
    navigateToTransactionEditScreen: () -> Unit,
    viewModel: TransactionsViewModel = hiltViewModel()
) {

    Scaffold(

        floatingActionButton = {
            FloatingActionButton(onClick = navigateToTransactionEditScreen) {
                Icon(imageVector = Icons.Filled.Add, contentDescription = "")
            }
        }

    ) {

        val transactions by viewModel.transactions.collectAsState()

        LazyColumn(modifier = Modifier.padding(it)) {

            items(
                count = transactions.size,
                key = { index ->
                    transactions[index].id
                }
            ) { index ->

                Text(text = transactions[index].description)

            }

        }

    }

}