package com.abhay.crypto.presentation.watchlist

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.abhay.crypto.domain.model.BookmarkFolder

/**
 * Holds the transient dialog/sheet visibility state for WatchlistContent.
 * Grouping it here keeps the composable parameter lists short and makes the
 * state easy to pass around without threading individual vars everywhere.
 */
@Stable
class WatchlistDialogState {
    var showCreateFolder by mutableStateOf(false)
    var folderToRename by mutableStateOf<BookmarkFolder?>(null)
    var coinForFolder by mutableStateOf<String?>(null)
    var coinToRemove by mutableStateOf<Pair<String, BookmarkFolder?>?>(null)
}
