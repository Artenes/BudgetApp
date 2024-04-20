package xyz.artenes.budget.app.transaction.list

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun TransactionsListScreen(
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

        LazyColumn(
            modifier = Modifier.padding(it)
        ) {

            items(
                count = transactions.size,
                key = { index ->
                    transactions[index].id
                }
            ) { index ->

                Surface(
                    color = Color.Transparent,
                    onClick = {  }
                ) {

                    Column {

                        Row(
                            modifier = Modifier.padding(20.dp)
                        ) {

                            Column {
                                Text(
                                    color = MaterialTheme.colorScheme.onBackground,
                                    text = transactions[index].description,
                                    style = MaterialTheme.typography.titleLarge
                                )
                                Text(
                                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                                    text = transactions[index].date,
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }

                            Spacer(modifier = Modifier.weight(1f))

                            Text(
                                color = MaterialTheme.colorScheme.onBackground,
                                text = "R$ ${transactions[index].amount}",
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

        }

    }

}