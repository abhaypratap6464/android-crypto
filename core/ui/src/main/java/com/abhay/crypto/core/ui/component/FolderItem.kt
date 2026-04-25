package com.abhay.crypto.core.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.AddToHomeScreen
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DriveFileRenameOutline
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.abhay.crypto.core.domain.model.BookmarkFolder
import com.abhay.crypto.core.ui.R
import com.abhay.crypto.core.ui.theme.Dimens
import com.abhay.crypto.core.ui.theme.FolderCardBackground
import com.abhay.crypto.core.ui.theme.SubtitleGray

@Composable
fun FolderItem(
    folder: BookmarkFolder,
    onRename: () -> Unit,
    onDelete: () -> Unit,
    onRemoveCoin: (coinId: String) -> Unit,
    onAddToHomeScreen: () -> Unit,
    priceProvider: (String) -> String,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(Dimens.RadiusMedium),
        colors = CardDefaults.cardColors(containerColor = FolderCardBackground),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Column(
            modifier = Modifier.padding(
                horizontal = Dimens.PaddingLarge,
                vertical = Dimens.PaddingDefault
            )
        ) {
            FolderItemHeader(
                name = folder.name,
                coinCount = folder.coinIds.size,
                onRename = onRename,
                onDelete = onDelete,
                onAddToHomeScreen = onAddToHomeScreen
            )

            if (folder.coinIds.isNotEmpty()) {
                FolderItemContent(
                    coinIds = folder.coinIds,
                    priceProvider = priceProvider,
                    onRemoveCoin = onRemoveCoin
                )
            }
        }
    }
}

@Composable
private fun FolderItemHeader(
    name: String,
    coinCount: Int,
    onRename: () -> Unit,
    onDelete: () -> Unit,
    onAddToHomeScreen: () -> Unit
) {
    var menuExpanded by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = name,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold,
        )

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = stringResource(R.string.coins, coinCount),
                style = MaterialTheme.typography.labelSmall,
                color = SubtitleGray,
            )

            IconButton(onClick = { menuExpanded = true }) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = stringResource(R.string.folder_options),
                )
            }

            FolderOptionsMenu(
                expanded = menuExpanded,
                onDismiss = { menuExpanded = false },
                onRename = onRename,
                onDelete = onDelete,
                onAddToHomeScreen = onAddToHomeScreen
            )
        }
    }
}

@Composable
private fun FolderOptionsMenu(
    expanded: Boolean,
    onDismiss: () -> Unit,
    onRename: () -> Unit,
    onDelete: () -> Unit,
    onAddToHomeScreen: () -> Unit
) {
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismiss,
    ) {
        DropdownMenuItem(
            text = { Text("Rename") },
            leadingIcon = {
                Icon(Icons.Default.DriveFileRenameOutline, contentDescription = null)
            },
            onClick = {
                onDismiss()
                onRename()
            },
        )
        DropdownMenuItem(
            text = { Text("Delete") },
            leadingIcon = {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error,
                )
            },
            onClick = {
                onDismiss()
                onDelete()
            },
        )
        DropdownMenuItem(
            text = { Text(stringResource(R.string.add_to_home_screen)) },
            leadingIcon = {
                Icon(
                    Icons.AutoMirrored.Filled.AddToHomeScreen,
                    contentDescription = null
                )
            },
            onClick = {
                onDismiss()
                onAddToHomeScreen()
            },
        )
    }
}

@Composable
private fun FolderItemContent(
    coinIds: List<String>,
    priceProvider: (String) -> String,
    onRemoveCoin: (String) -> Unit
) {
    Spacer(modifier = Modifier.height(Dimens.SpacingMedium))
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(Dimens.SpacingMedium)
    ) {
        coinIds.forEach { symbol ->
            FolderCoinItem(
                symbol = symbol,
                price = priceProvider(symbol),
                onRemove = { onRemoveCoin(symbol) }
            )
        }
    }
}

@Composable
private fun FolderCoinItem(
    symbol: String,
    price: String,
    onRemove: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = Dimens.PaddingSmall),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = symbol.removeSuffix(stringResource(R.string.usdt)),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = price,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.width(Dimens.SpacingMedium))
        IconButton(
            onClick = onRemove,
            modifier = Modifier.size(Dimens.IconMedium)
        ) {
            Icon(
                imageVector = Icons.Default.Bookmark,
                contentDescription = stringResource(R.string.remove_from_folder),
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(Dimens.IconSmall)
            )
        }
    }
}
