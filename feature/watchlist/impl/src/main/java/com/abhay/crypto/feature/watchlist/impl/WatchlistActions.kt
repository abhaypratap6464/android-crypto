package com.abhay.crypto.feature.watchlist.impl

import androidx.compose.runtime.Stable
import com.abhay.crypto.core.domain.model.BookmarkFolder

// A plain stable class — data class is wrong here because lambda equals() is
// always reference-based, so data class would never produce a stable cached instance.
@Stable
class WatchlistActions(
    val onEvent: (WatchlistUiEvent) -> Unit,
    val priceProvider: (String) -> Double?,
    val formatPrice: (Double) -> String,
    val onRenameFolder: (BookmarkFolder) -> Unit,
    val onAddToHomeScreen: (folderId: String) -> Unit,
    val onCoinForFolder: (String) -> Unit,
    val onRemoveCoinFromFolder: (String, BookmarkFolder) -> Unit,
)
