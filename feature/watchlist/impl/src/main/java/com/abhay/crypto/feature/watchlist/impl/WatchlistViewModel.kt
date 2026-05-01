package com.abhay.crypto.feature.watchlist.impl

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.abhay.crypto.core.domain.Constants
import com.abhay.crypto.core.domain.model.Coin
import com.abhay.crypto.core.domain.usecase.FormatPriceUseCase
import com.abhay.crypto.core.domain.usecase.GetPagedCoinsUseCase
import com.abhay.crypto.core.domain.usecase.ObserveLivePricesUseCase
import com.abhay.crypto.core.domain.usecase.ObserveNetworkUseCase
import com.abhay.crypto.core.domain.usecase.folder.AddBookmarkToFolderUseCase
import com.abhay.crypto.core.domain.usecase.folder.CreateFolderUseCase
import com.abhay.crypto.core.domain.usecase.folder.DeleteFolderUseCase
import com.abhay.crypto.core.domain.usecase.folder.GetFoldersUseCase
import com.abhay.crypto.core.domain.usecase.folder.RemoveBookmarkFromFolderUseCase
import com.abhay.crypto.core.domain.usecase.folder.RenameFolderUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WatchlistViewModel @Inject constructor(
    getPagedCoins: GetPagedCoinsUseCase,
    observeLivePrices: ObserveLivePricesUseCase,
    observeNetwork: ObserveNetworkUseCase,
    getFolders: GetFoldersUseCase,
    private val formatPriceUseCase: FormatPriceUseCase,
    private val createFolder: CreateFolderUseCase,
    private val renameFolder: RenameFolderUseCase,
    private val deleteFolder: DeleteFolderUseCase,
    private val addBookmarkToFolder: AddBookmarkToFolderUseCase,
    private val removeBookmarkFromFolder: RemoveBookmarkFromFolderUseCase,
) : ViewModel() {

    val pagedCoins: Flow<PagingData<Coin>> = getPagedCoins().cachedIn(viewModelScope)

    val uiState: StateFlow<WatchlistUiState> = combine(
        getFolders(),
        observeNetwork()
    ) { folders, isNetworkAvailable ->
        WatchlistUiState(
            folders = folders,
            isNetworkAvailable = isNetworkAvailable,
            coinIdsInFolders = folders.flatMap { it.coinIds }.toSet()
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(Constants.SUBSCRIPTION_TIMEOUT_MS),
        initialValue = WatchlistUiState()
    )

    val livePrices: StateFlow<Map<String, Double>> =
        observeLivePrices(uiState.map { it.coinIdsInFolders })
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(Constants.SUBSCRIPTION_TIMEOUT_MS),
                initialValue = emptyMap()
            )

    fun formatPrice(price: Double): String = formatPriceUseCase(price)

    fun onEvent(event: WatchlistUiEvent) {
        viewModelScope.launch {
            when (event) {
                is WatchlistUiEvent.CreateFolder -> createFolder(event.name, event.coinId)
                is WatchlistUiEvent.RenameFolder -> renameFolder(event.folderId, event.newName)
                is WatchlistUiEvent.DeleteFolder -> deleteFolder(event.folderId)
                is WatchlistUiEvent.AddBookmarkToFolder -> addBookmarkToFolder(
                    event.folderId,
                    event.coinId
                )

                is WatchlistUiEvent.RemoveBookmarkFromFolder -> removeBookmarkFromFolder(
                    event.folderId,
                    event.coinId
                )
            }
        }
    }
}
