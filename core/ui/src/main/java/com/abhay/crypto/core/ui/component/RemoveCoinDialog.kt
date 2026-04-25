package com.abhay.crypto.core.ui.component

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.abhay.crypto.core.ui.R

@Composable
fun RemoveCoinDialog(
    coinId: String,
    folderName: String?,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.remove_coin)) },
        text = {
            val message = if (folderName != null) {
                stringResource(
                    R.string.are_you_sure_you_want_to_remove_from,
                    coinId.removeSuffix(stringResource(R.string.usdt)),
                    folderName
                )
            } else {
                stringResource(
                    R.string.are_you_sure_you_want_to_remove_from_your_watchlist,
                    coinId.removeSuffix(stringResource(R.string.usdt))
                )
            }
            Text(message)
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(stringResource(R.string.remove))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        },
    )
}
