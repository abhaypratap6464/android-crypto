package com.abhay.crypto.presentation.watchlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.abhay.crypto.domain.model.Coin
import com.abhay.crypto.domain.usecase.FormatPriceUseCase
import com.abhay.crypto.domain.usecase.GetPagedCoinsUseCase
import com.abhay.crypto.domain.usecase.GetWatchListedUseCase
import com.abhay.crypto.domain.usecase.ObserveLivePricesUseCase
import com.abhay.crypto.domain.usecase.ObserveNetworkUseCase
import com.abhay.crypto.domain.usecase.ToggleWatchListUseCase
import com.abhay.crypto.domain.usecase.folder.AddBookmarkToFolderUseCase
import com.abhay.crypto.domain.usecase.folder.CreateFolderUseCase
import com.abhay.crypto.domain.usecase.folder.DeleteFolderUseCase
import com.abhay.crypto.domain.usecase.folder.GetFoldersUseCase
import com.abhay.crypto.domain.usecase.folder.RemoveBookmarkFromFolderUseCase
import com.abhay.crypto.domain.usecase.folder.RenameFolderUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val SUBSCRIPTION_TIMEOUT_MS = 5_000L

@HiltViewModel
class WatchlistViewModel @Inject constructor(
    getPagedCoins: GetPagedCoinsUseCase,
    observeLivePrices: ObserveLivePricesUseCase,
    observeNetwork: ObserveNetworkUseCase,
    getFolders: GetFoldersUseCase,
    getWatchListed: GetWatchListedUseCase,
    val formatPrice: FormatPriceUseCase,
    private val toggleWatchList: ToggleWatchListUseCase,
    private val createFolder: CreateFolderUseCase,
    private val renameFolder: RenameFolderUseCase,
    private val deleteFolder: DeleteFolderUseCase,
    private val addBookmarkToFolder: AddBookmarkToFolderUseCase,
    private val removeBookmarkFromFolder: RemoveBookmarkFromFolderUseCase,
) : ViewModel() {

    val pagedCoins: Flow<PagingData<Coin>> = getPagedCoins().cachedIn(viewModelScope)

    val livePrices: StateFlow<Map<String, Double>> = observeLivePrices().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(SUBSCRIPTION_TIMEOUT_MS),
        initialValue = emptyMap(),
    )

    val uiState: StateFlow<WatchlistUiState> = combine(
        observeNetwork(),
        getFolders(),
        getWatchListed(),
    ) { isNetworkAvailable, folders, watchListed ->
        WatchlistUiState(
            isNetworkAvailable = isNetworkAvailable,
            folders = folders,
            watchListed = watchListed,
            coinIdsInFolders = folders.flatMapTo(mutableSetOf()) { it.coinIds },
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(SUBSCRIPTION_TIMEOUT_MS),
        initialValue = WatchlistUiState(),
    )

    fun onEvent(event: WatchlistUiEvent) {
        when (event) {
            is WatchlistUiEvent.ToggleWatchList -> viewModelScope.launch {
                toggleWatchList(event.symbol)
            }

            is WatchlistUiEvent.CreateFolder -> viewModelScope.launch {
                createFolder(event.name, event.coinId)
            }

            is WatchlistUiEvent.RenameFolder -> viewModelScope.launch {
                renameFolder(event.folderId, event.newName)
            }

            is WatchlistUiEvent.DeleteFolder -> viewModelScope.launch {
                deleteFolder(event.folderId)
            }

            is WatchlistUiEvent.AddBookmarkToFolder -> viewModelScope.launch {
                val folder = uiState.value.folders.find { it.id == event.folderId }
                if (folder?.coinIds?.contains(event.coinId) == true) {
                    removeBookmarkFromFolder(event.folderId, event.coinId)
                } else {
                    addBookmarkToFolder(event.folderId, event.coinId)
                }
            }

            is WatchlistUiEvent.RemoveBookmarkFromFolder -> viewModelScope.launch {
                removeBookmarkFromFolder(event.folderId, event.coinId)
            }
        }
    }
}
