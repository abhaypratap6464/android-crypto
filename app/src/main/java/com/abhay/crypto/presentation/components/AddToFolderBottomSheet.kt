package com.abhay.crypto.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.unit.dp
import com.abhay.crypto.R
import com.abhay.crypto.domain.model.BookmarkFolder


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
    val displayName = coinId.removeSuffix("USDT")

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(bottom = 16.dp),
        ) {
            Text(
                text = stringResource(R.string.add_to_folder, displayName),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp),
            )

            HorizontalDivider()

            if (folders.isEmpty()) {
                Text(
                    text = "No folders yet",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp),
                )
            } else {
                LazyColumn {
                    items(folders, key = { it.id }) { folder ->
                        val isInFolder = folder.coinIds.contains(coinId)

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onAddToFolder(folder.id) }
                                .padding(horizontal = 20.dp, vertical = 14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                        ) {
                            Icon(
                                imageVector = if (isInFolder) Icons.Default.CheckCircle
                                else Icons.Outlined.Folder,
                                contentDescription = null,
                                tint = if (isInFolder) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(24.dp),
                            )
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = folder.name,
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = if (isInFolder) MaterialTheme.colorScheme.primary
                                    else MaterialTheme.colorScheme.onSurface,
                                )
                                Text(
                                    text = if (isInFolder) "Added — tap to remove"
                                    else "${folder.coinIds.size} coin${if (folder.coinIds.size == 1) "" else "s"}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = if (isInFolder) MaterialTheme.colorScheme.primary.copy(
                                        alpha = 0.7f
                                    )
                                    else Color.Gray,
                                )
                            }
                        }

                        HorizontalDivider(modifier = Modifier.padding(horizontal = 20.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            TextButton(
                onClick = onCreateFolder,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp),
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                )
                Spacer(modifier = Modifier.size(8.dp))
                Text(stringResource(R.string.create_new_folder))
            }
        }
    }
}
