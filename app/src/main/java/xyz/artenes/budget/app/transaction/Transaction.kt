package xyz.artenes.budget.app.transaction

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import xyz.artenes.budget.app.theme.CustomColorScheme
import xyz.artenes.budget.core.models.TransactionItem

@Composable
fun Transaction(
    transaction: TransactionItem,
    onClicked: (TransactionItem) -> Unit,
    showDate: Boolean = false
) {

    Surface(
        color = Color.Transparent,
        onClick = { onClicked(transaction) }
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
                            CustomColorScheme.border(),
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    /*
                    Category
                     */
                    Icon(
                        imageVector = transaction.icon,
                        contentDescription = "",
                        tint = CustomColorScheme.icon()
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column(
                    modifier = Modifier.weight(2f)
                ) {

                    if (showDate) {
                        /*
                        Date
                         */
                        Text(
                            color = CustomColorScheme.textColorLight(),
                            text = transaction.formattedDate,
                            style = MaterialTheme.typography.labelSmall
                        )
                    }

                    /*
                    Description
                     */
                    Text(
                        color = CustomColorScheme.textColor(),
                        text = transaction.description,
                        style = MaterialTheme.typography.titleLarge
                    )

                }

                /*
                Amount
                 */
                Text(
                    color = CustomColorScheme.textColor(alpha = transaction.colorAlpha),
                    text = transaction.formattedAmount,
                    style = MaterialTheme.typography.titleLarge,
                    textAlign = TextAlign.End,
                    maxLines = 1
                )

            }

            HorizontalDivider(
                thickness = 2.dp,
                color = CustomColorScheme.textColorExtraLight()
            )

        }

    }

}