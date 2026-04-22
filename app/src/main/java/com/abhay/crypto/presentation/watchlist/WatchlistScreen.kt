package com.abhay.crypto.presentation.watchlist

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import com.abhay.crypto.R
import com.abhay.crypto.domain.model.BookmarkFolder
import com.abhay.crypto.domain.model.Coin
import com.abhay.crypto.presentation.components.AddToFolderBottomSheet
import com.abhay.crypto.presentation.components.CoinListItem
import com.abhay.crypto.presentation.components.CreateFolderDialog
import com.abhay.crypto.presentation.components.ErrorView
import com.abhay.crypto.presentation.components.FolderItem
import com.abhay.crypto.presentation.components.LoadingView
import com.abhay.crypto.presentation.components.NetworkBanner
import com.abhay.crypto.presentation.components.RemoveCoinDialog
import com.abhay.crypto.presentation.components.RenameFolderDialog

private val ScreenBackground = Color(0xFFF0EEF5)

@Composable
fun WatchlistScreen(viewModel: WatchlistViewModel = hiltViewModel()) {
    val lazyPagingItems = viewModel.pagedCoins.collectAsLazyPagingItems()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val livePrices by viewModel.livePrices.collectAsStateWithLifecycle()

    WatchlistContent(
        lazyPagingItems = lazyPagingItems,
        uiState = uiState,
        onEvent = viewModel::onEvent,
        priceProvider = { symbol -> livePrices[symbol] },
        formatPrice = { viewModel.formatPrice(it) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun WatchlistContent(
    lazyPagingItems: LazyPagingItems<Coin>,
    uiState: WatchlistUiState,
    onEvent: (WatchlistUiEvent) -> Unit,
    priceProvider: (String) -> Double?,
    formatPrice: (Double) -> String,
) {
    val listState = rememberLazyListState()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    var showCreateFolderDialog by rememberSaveable { mutableStateOf(false) }
    var folderToRename by remember { mutableStateOf<BookmarkFolder?>(null) }
    var coinForFolder by rememberSaveable { mutableStateOf<String?>(null) }
    var pendingCoinForNewFolder by rememberSaveable { mutableStateOf<String?>(null) }
    var coinToRemove by remember { mutableStateOf<Pair<String, BookmarkFolder?>?>(null) }

    // Dialogs & BottomSheets
    coinToRemove?.let { (coinId, folder) ->
        RemoveCoinDialog(
            coinId = coinId,
            folderName = folder?.name,
            onConfirm = {
                if (folder != null) {
                    onEvent(WatchlistUiEvent.RemoveBookmarkFromFolder(folder.id, coinId))
                }
                coinToRemove = null
            },
            onDismiss = { coinToRemove = null }
        )
    }

    if (showCreateFolderDialog) {
        CreateFolderDialog(
            onConfirm = { name ->
                onEvent(WatchlistUiEvent.CreateFolder(name, pendingCoinForNewFolder))
                pendingCoinForNewFolder = null
                showCreateFolderDialog = false
            },
            onDismiss = {
                pendingCoinForNewFolder = null
                showCreateFolderDialog = false
            },
        )
    }

    folderToRename?.let { folder ->
        RenameFolderDialog(
            currentName = folder.name,
            onConfirm = { newName ->
                onEvent(WatchlistUiEvent.RenameFolder(folder.id, newName))
                folderToRename = null
            },
            onDismiss = { folderToRename = null },
        )
    }

    coinForFolder?.let { coinId ->
        AddToFolderBottomSheet(
            coinId = coinId,
            folders = uiState.folders,
            onAddToFolder = { folderId ->
                onEvent(WatchlistUiEvent.AddBookmarkToFolder(folderId, coinId))
                coinForFolder = null
            },
            onCreateFolder = {
                pendingCoinForNewFolder = coinId
                coinForFolder = null
                showCreateFolderDialog = true
            },
            onDismiss = { coinForFolder = null },
        )
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        containerColor = ScreenBackground,
        topBar = {
            WatchlistTopBar(
                onAddFolder = { showCreateFolderDialog = true },
                scrollBehavior = scrollBehavior
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (!uiState.isNetworkAvailable) {
                NetworkBanner()
            }

            WatchlistMainContent(
                lazyPagingItems = lazyPagingItems,
                uiState = uiState,
                listState = listState,
                onEvent = onEvent,
                priceProvider = priceProvider,
                formatPrice = formatPrice,
                onRenameFolder = { folderToRename = it },
                onCoinForFolder = { coinId ->
                    val foldersWithCoin = uiState.folders.filter { it.coinIds.contains(coinId) }
                    if (foldersWithCoin.size == 1) {
                        coinToRemove = coinId to foldersWithCoin[0]
                    } else {
                        coinForFolder = coinId
                    }
                },
                onRemoveCoinFromFolder = { coinId, folder ->
                    coinToRemove = coinId to folder
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun WatchlistTopBar(
    onAddFolder: () -> Unit,
    scrollBehavior: androidx.compose.material3.TopAppBarScrollBehavior
) {
    TopAppBar(
        title = {
            Text(
                text = stringResource(R.string.watchlist),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
            )
        },
        actions = {
            IconButton(onClick = onAddFolder) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(R.string.new_folder),
                    modifier = Modifier.size(28.dp),
                )
            }
        },
        scrollBehavior = scrollBehavior,
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = ScreenBackground,
            scrolledContainerColor = ScreenBackground
        )
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun WatchlistMainContent(
    lazyPagingItems: LazyPagingItems<Coin>,
    uiState: WatchlistUiState,
    listState: LazyListState,
    onEvent: (WatchlistUiEvent) -> Unit,
    priceProvider: (String) -> Double?,
    formatPrice: (Double) -> String,
    onRenameFolder: (BookmarkFolder) -> Unit,
    onCoinForFolder: (String) -> Unit,
    onRemoveCoinFromFolder: (String, BookmarkFolder) -> Unit,
) {
    val isRefreshing = remember(lazyPagingItems.loadState.refresh, lazyPagingItems.itemCount) {
        lazyPagingItems.loadState.refresh is LoadState.Loading && lazyPagingItems.itemCount > 0
    }

    when (val refreshState = lazyPagingItems.loadState.refresh) {
        is LoadState.Loading -> {
            if (lazyPagingItems.itemCount == 0) {
                LoadingView()
            }
        }

        is LoadState.Error -> {
            ErrorView(
                error = refreshState.error,
                onRetry = { lazyPagingItems.refresh() }
            )
        }

        is LoadState.NotLoading -> {
            PullToRefreshBox(
                isRefreshing = isRefreshing,
                onRefresh = { lazyPagingItems.refresh() },
                modifier = Modifier.fillMaxSize(),
            ) {
                WatchlistList(
                    lazyPagingItems = lazyPagingItems,
                    uiState = uiState,
                    listState = listState,
                    onEvent = onEvent,
                    priceProvider = priceProvider,
                    formatPrice = formatPrice,
                    onRenameFolder = onRenameFolder,
                    onCoinForFolder = onCoinForFolder,
                    onRemoveCoinFromFolder = onRemoveCoinFromFolder
                )
            }
        }
    }
}

@Composable
private fun WatchlistList(
    lazyPagingItems: LazyPagingItems<Coin>,
    uiState: WatchlistUiState,
    listState: LazyListState,
    onEvent: (WatchlistUiEvent) -> Unit,
    priceProvider: (String) -> Double?,
    formatPrice: (Double) -> String,
    onRenameFolder: (BookmarkFolder) -> Unit,
    onCoinForFolder: (String) -> Unit,
    onRemoveCoinFromFolder: (String, BookmarkFolder) -> Unit,
) {
    LazyColumn(
        state = listState,
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxSize(),
    ) {
        if (uiState.folders.isNotEmpty()) {
            item(contentType = "header") {
                Text(
                    text = stringResource(R.string.my_folders),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                )
            }

            items(
                items = uiState.folders,
                key = { it.id },
                contentType = { "folder" }
            ) { folder ->
                FolderItem(
                    modifier = Modifier.animateItem(),
                    folder = folder,
                    onRename = { onRenameFolder(folder) },
                    onDelete = { onEvent(WatchlistUiEvent.DeleteFolder(folder.id)) },
                    onRemoveCoin = { coinId ->
                        onRemoveCoinFromFolder(coinId, folder)
                    },
                    priceProvider = { symbol ->
                        val price = priceProvider(symbol) ?: 0.0
                        formatPrice(price)
                    }
                )
            }

            item { Spacer(modifier = Modifier.height(4.dp)) }
        }

        item(contentType = "header") {
            Text(
                text = stringResource(R.string.all_coins),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            )
        }

        items(
            count = lazyPagingItems.itemCount,
            key = lazyPagingItems.itemKey { it.symbol },
            contentType = lazyPagingItems.itemContentType { "coin" }
        ) { index ->
            val coin = lazyPagingItems[index]
            coin?.let {
                CoinListItem(
                    modifier = Modifier.animateItem(),
                    coin = it,
                    priceProvider = {
                        val price = priceProvider(it.symbol) ?: it.price
                        formatPrice(price)
                    },
                    isFoldered = it.symbol in uiState.coinIdsInFolders,
                    onAddToFolder = { onCoinForFolder(it.symbol) },
                )
            }
        }

        if (lazyPagingItems.loadState.append is LoadState.Loading) {
            item(contentType = "loading_footer") {
                CircularProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp)
                        .wrapContentWidth(Alignment.CenterHorizontally),
                )
            }
        }
    }
}
