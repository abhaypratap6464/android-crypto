package com.abhay.crypto.presentation.watchlist

import com.abhay.crypto.domain.model.BookmarkFolder

data class WatchlistActions(
    val onEvent: (WatchlistUiEvent) -> Unit,
    val priceProvider: (String) -> Double?,
    val formatPrice: (Double) -> String,
    val onRenameFolder: (BookmarkFolder) -> Unit,
    val onAddToHomeScreen: (folderId: String) -> Unit,
    val onCoinForFolder: (String) -> Unit,
    val onRemoveCoinFromFolder: (String, BookmarkFolder) -> Unit,
)
