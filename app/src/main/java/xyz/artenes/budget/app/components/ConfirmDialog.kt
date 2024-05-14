package xyz.artenes.budget.app.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import xyz.artenes.budget.R
import xyz.artenes.budget.app.theme.CustomColorScheme

@Composable
fun ConfirmDialog(
    show: Boolean,
    title: String,
    body: String,
    onDismiss: () -> Unit,
    onConfirm: (() -> Unit)? = null,
    confirmText: String = stringResource(R.string.confirm)
) {

    if (!show) {
        return
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = { Text(body) },
        confirmButton = {
            Button(
                onClick = {
                    onConfirm?.let { it() }
                    onDismiss()
                }
            ) {
                Text(
                    text = confirmText,
                    color = CustomColorScheme.textColor()
                )
            }
        },
        dismissButton = if (onConfirm != null) {
            {
                OutlinedButton(
                    onClick = onDismiss
                ) {
                    Text(
                        text = stringResource(R.string.cancel),
                        color = CustomColorScheme.textColor()
                    )
                }
            }
        } else null
    )

}