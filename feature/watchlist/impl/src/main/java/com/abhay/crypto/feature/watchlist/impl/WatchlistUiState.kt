package com.abhay.crypto.feature.watchlist.impl

import com.abhay.crypto.core.domain.model.BookmarkFolder

data class WatchlistUiState(
    val isNetworkAvailable: Boolean = true,
    val folders: List<BookmarkFolder> = emptyList(),
    val coinIdsInFolders: Set<String> = emptySet(),
)
