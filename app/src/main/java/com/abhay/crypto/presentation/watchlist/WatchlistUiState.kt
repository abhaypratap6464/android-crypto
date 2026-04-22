package com.abhay.crypto.presentation.watchlist

import com.abhay.crypto.domain.model.BookmarkFolder

data class WatchlistUiState(
    val isNetworkAvailable: Boolean = true,
    val folders: List<BookmarkFolder> = emptyList(),
    val watchListed: Set<String> = emptySet(),
    val coinIdsInFolders: Set<String> = emptySet(),
)
