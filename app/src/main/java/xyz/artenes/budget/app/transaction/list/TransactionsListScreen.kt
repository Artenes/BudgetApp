package xyz.artenes.budget.app.transaction.list

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import xyz.artenes.budget.core.TransactionType
import xyz.artenes.budget.data.TransactionWithCategoryEntity
import xyz.artenes.budget.utils.LocaleFormatter

@Composable
fun TransactionsListScreen(
    localeFormatter: LocaleFormatter,
    navigateToTransactionEditScreen: () -> Unit,
    viewModel: TransactionsListViewModel = hiltViewModel()
) {

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = navigateToTransactionEditScreen) {
                Icon(imageVector = Icons.Filled.Add, contentDescription = "")
            }
        }

    ) {

        val transactionGroups by viewModel.transactions.collectAsState()
        val total by viewModel.total.collectAsState()

        LazyColumn(
            modifier = Modifier.padding(it)
        ) {

            /*
            Total amount based on current filters
             */
            item {

                Column(
                    modifier = Modifier.padding(horizontal = 20.dp)
                ) {

                    Spacer(modifier = Modifier.height(150.dp))

                    Text(
                        text = "Total of expenses this month",
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                        style = MaterialTheme.typography.bodySmall,
                        fontSize = 12.sp
                    )
                    Text(
                        text = "${localeFormatter.getCurrencySymbol()} ${
                            total.format(
                                localeFormatter.getMoneyFormat()
                            )
                        }",
                        color = MaterialTheme.colorScheme.onBackground,
                        style = MaterialTheme.typography.titleLarge,
                        fontSize = 30.sp
                    )

                    Spacer(modifier = Modifier.height(100.dp))

                }

            }

            transactionGroups.forEach { group ->

                /*
                Date
                 */
                item {
                    val relativeDate = localeFormatter.formatDateAsRelative(group.date)
                    Row(
                        modifier = Modifier.padding(horizontal = 20.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            modifier = Modifier.padding(end = 10.dp),
                            color = MaterialTheme.colorScheme.onBackground,
                            text = if (relativeDate.isRelative) {
                                relativeDate.relative
                            } else {
                                relativeDate.absolute
                            },
                            style = MaterialTheme.typography.bodyLarge
                        )
                        if (relativeDate.isRelative) {
                            Text(
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                                text = relativeDate.absolute,
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
                        localeFormatter = localeFormatter
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

}

@Composable
private fun Transaction(
    localeFormatter: LocaleFormatter,
    transaction: TransactionWithCategoryEntity
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
                val color = if (transaction.type == TransactionType.EXPENSE) {
                    MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                } else {
                    MaterialTheme.colorScheme.onBackground
                }

                val sign = if (transaction.type == TransactionType.EXPENSE) {
                    "-"
                } else {
                    "+"
                }

                Text(
                    color = color,
                    text = "$sign ${localeFormatter.getCurrencySymbol()} ${
                        transaction.amount.format(
                            localeFormatter.getMoneyFormat()
                        )
                    }",
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