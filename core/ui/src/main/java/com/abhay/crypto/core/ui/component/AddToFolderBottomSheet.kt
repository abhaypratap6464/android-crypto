package com.abhay.crypto.core.ui.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.abhay.crypto.core.domain.model.BookmarkFolder
import com.abhay.crypto.core.ui.R
import com.abhay.crypto.core.ui.theme.Dimens

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddToFolderBottomSheet(
    coinId: String,
    folders: List<BookmarkFolder>,
    onAddToFolder: (folderId: String) -> Unit,
    onCreateFolder: () -> Unit,
    onDismiss: () -> Unit,
) {
    val sheetState = rememberModalBottomSheetState()
    ModalBottomSheet(onDismissRequest = onDismiss, sheetState = sheetState) {
        BottomSheetContent(
            coinId = coinId,
            folders = folders,
            onAddToFolder = onAddToFolder,
            onCreateFolder = onCreateFolder
        )
    }
}

@Composable
private fun BottomSheetContent(
    coinId: String,
    folders: List<BookmarkFolder>,
    onAddToFolder: (String) -> Unit,
    onCreateFolder: () -> Unit
) {
    val displayName = coinId.removeSuffix("USDT")
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(bottom = Dimens.PaddingExtraLarge),
    ) {
        BottomSheetHeader(displayName)
        HorizontalDivider()

        if (folders.isEmpty()) {
            EmptyFoldersText()
        } else {
            FolderList(coinId, folders, onAddToFolder)
        }

        Spacer(modifier = Modifier.height(Dimens.SpacingSmall))
        CreateFolderButton(onCreateFolder)
    }
}

@Composable
private fun BottomSheetHeader(displayName: String) {
    Text(
        text = stringResource(R.string.add_to_folder, displayName),
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier.padding(
            horizontal = Dimens.PaddingExtraExtraLarge,
            vertical = Dimens.PaddingMedium
        ),
    )
}

@Composable
private fun EmptyFoldersText() {
    Text(
        text = "No folders yet",
        style = MaterialTheme.typography.bodyMedium,
        color = Color.Gray,
        modifier = Modifier.padding(
            horizontal = Dimens.PaddingExtraExtraLarge,
            vertical = Dimens.PaddingLarge
        ),
    )
}

@Composable
private fun FolderList(
    coinId: String,
    folders: List<BookmarkFolder>,
    onAddToFolder: (String) -> Unit
) {
    LazyColumn {
        items(folders, key = { it.id }) { folder ->
            FolderItemRow(
                coinId = coinId,
                folder = folder,
                onAddToFolder = onAddToFolder
            )
            HorizontalDivider(modifier = Modifier.padding(horizontal = Dimens.PaddingExtraExtraLarge))
        }
    }
}

@Composable
private fun FolderItemRow(
    coinId: String,
    folder: BookmarkFolder,
    onAddToFolder: (String) -> Unit
) {
    val isInFolder = folder.coinIds.contains(coinId)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onAddToFolder(folder.id) }
            .padding(horizontal = Dimens.PaddingExtraExtraLarge, vertical = Dimens.PaddingVertical),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Dimens.PaddingExtraLarge),
    ) {
        FolderIcon(isInFolder)
        FolderInfo(folder, isInFolder)
    }
}

@Composable
private fun FolderIcon(isInFolder: Boolean) {
    val icon = if (isInFolder) Icons.Default.CheckCircle else Icons.Outlined.Folder
    val tint = if (isInFolder) MaterialTheme.colorScheme.primary
    else MaterialTheme.colorScheme.onSurfaceVariant

    Icon(
        imageVector = icon,
        contentDescription = null,
        tint = tint,
        modifier = Modifier.size(Dimens.IconMedium),
    )
}

@Composable
private fun RowScope.FolderInfo(folder: BookmarkFolder, isInFolder: Boolean) {
    val primaryColor = MaterialTheme.colorScheme.primary
    val onSurfaceColor = MaterialTheme.colorScheme.onSurface

    Column(modifier = Modifier.weight(1f)) {
        Text(
            text = folder.name,
            style = MaterialTheme.typography.bodyLarge,
            color = if (isInFolder) primaryColor else onSurfaceColor
        )
        val subText = if (isInFolder) "Added — tap to remove"
        else "${folder.coinIds.size} coin${if (folder.coinIds.size == 1) "" else "s"}"
        val subTextColor = if (isInFolder) primaryColor.copy(alpha = 0.7f)
        else Color.Gray
        Text(text = subText, style = MaterialTheme.typography.bodySmall, color = subTextColor)
    }
}

@Composable
private fun CreateFolderButton(onCreateFolder: () -> Unit) {
    TextButton(
        onClick = onCreateFolder,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Dimens.PaddingLarge),
    ) {
        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = null,
            modifier = Modifier.size(Dimens.IconSmall),
        )
        Spacer(modifier = Modifier.size(Dimens.SpacingMedium))
        Text(stringResource(R.string.create_new_folder))
    }
}
