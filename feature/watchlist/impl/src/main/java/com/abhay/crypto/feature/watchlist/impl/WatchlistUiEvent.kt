package com.abhay.crypto.feature.watchlist.impl

sealed interface WatchlistUiEvent {
    data class CreateFolder(val name: String, val coinId: String? = null) : WatchlistUiEvent
    data class RenameFolder(val folderId: String, val newName: String) : WatchlistUiEvent
    data class DeleteFolder(val folderId: String) : WatchlistUiEvent
    data class AddBookmarkToFolder(val folderId: String, val coinId: String) : WatchlistUiEvent
    data class RemoveBookmarkFromFolder(val folderId: String, val coinId: String) : WatchlistUiEvent
}
