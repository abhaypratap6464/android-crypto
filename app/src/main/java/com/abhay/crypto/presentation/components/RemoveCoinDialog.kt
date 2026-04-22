package com.abhay.crypto.presentation.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable

@Composable
fun RemoveCoinDialog(
    coinId: String,
    folderName: String?,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Remove Coin") },
        text = {
            val message = if (folderName != null) {
                "Are you sure you want to remove ${coinId.removeSuffix("USDT")} from '$folderName'?"
            } else {
                "Are you sure you want to remove ${coinId.removeSuffix("USDT")} from your watchlist?"
            }
            Text(message)
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("Remove")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
    )
}
