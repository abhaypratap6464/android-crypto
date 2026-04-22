package com.abhay.crypto.presentation.watchlist

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
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
import androidx.compose.ui.platform.LocalContext
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
import com.abhay.crypto.presentation.glance.widget.CryptoWidgetReceiver
import com.abhay.crypto.presentation.glance.widget.WidgetPinReceiver

@Suppress("MagicNumber")
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
internal fun WatchlistContent(
    lazyPagingItems: LazyPagingItems<Coin>,
    uiState: WatchlistUiState,
    onEvent: (WatchlistUiEvent) -> Unit,
    priceProvider: (String) -> Double?,
    formatPrice: (Double) -> String,
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    // Stable state holder — keeps WatchlistDialogs parameter count under the threshold.
    val dialogState = remember { WatchlistDialogState() }

    WatchlistDialogs(
        uiState = uiState,
        onEvent = onEvent,
        dialogState = dialogState,
    )

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        containerColor = ScreenBackground,
        topBar = {
            WatchlistTopBar(
                onAddFolder = { dialogState.showCreateFolder = true },
                scrollBehavior = scrollBehavior,
            )
        }
    ) { paddingValues ->
        val context = LocalContext.current
        // dialogState is a stable val — safe to capture inside remember.
        val actions = remember(onEvent, priceProvider, formatPrice, context) {
            WatchlistActions(
                onEvent = onEvent,
                priceProvider = priceProvider,
                formatPrice = formatPrice,
                onRenameFolder = { dialogState.folderToRename = it },
                onAddToHomeScreen = { folderId -> requestPinWidget(context, folderId) },
                onCoinForFolder = { dialogState.coinForFolder = it },
                onRemoveCoinFromFolder = { coinId, folder ->
                    dialogState.coinToRemove = coinId to folder
                },
            )
        }

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
                actions = actions,
            )
        }
    }
}

@Composable
private fun WatchlistDialogs(
    uiState: WatchlistUiState,
    onEvent: (WatchlistUiEvent) -> Unit,
    dialogState: WatchlistDialogState,
) {
    var pendingCoinForNewFolder by rememberSaveable { mutableStateOf<String?>(null) }

    dialogState.coinToRemove?.let { (coinId, folder) ->
        RemoveCoinDialog(
            coinId = coinId,
            folderName = folder?.name,
            onConfirm = {
                if (folder != null) {
                    onEvent(WatchlistUiEvent.RemoveBookmarkFromFolder(folder.id, coinId))
                }
                dialogState.coinToRemove = null
            },
            onDismiss = { dialogState.coinToRemove = null },
        )
    }

    if (dialogState.showCreateFolder) {
        CreateFolderDialog(
            onConfirm = { name ->
                onEvent(WatchlistUiEvent.CreateFolder(name, pendingCoinForNewFolder))
                pendingCoinForNewFolder = null
                dialogState.showCreateFolder = false
            },
            onDismiss = {
                pendingCoinForNewFolder = null
                dialogState.showCreateFolder = false
            },
        )
    }

    dialogState.folderToRename?.let { folder ->
        RenameFolderDialog(
            currentName = folder.name,
            onConfirm = { newName ->
                onEvent(WatchlistUiEvent.RenameFolder(folder.id, newName))
                dialogState.folderToRename = null
            },
            onDismiss = { dialogState.folderToRename = null },
        )
    }

    dialogState.coinForFolder?.let { coinId ->
        AddToFolderBottomSheet(
            coinId = coinId,
            folders = uiState.folders,
            onAddToFolder = { folderId ->
                onEvent(WatchlistUiEvent.AddBookmarkToFolder(folderId, coinId))
                dialogState.coinForFolder = null
            },
            onCreateFolder = {
                pendingCoinForNewFolder = coinId
                dialogState.coinForFolder = null
                dialogState.showCreateFolder = true
            },
            onDismiss = { dialogState.coinForFolder = null },
        )
    }
}

private fun requestPinWidget(context: Context, folderId: String) {
    val manager = AppWidgetManager.getInstance(context)
    val provider = ComponentName(context, CryptoWidgetReceiver::class.java)
    if (manager.isRequestPinAppWidgetSupported) {
        val callbackIntent = Intent(context, WidgetPinReceiver::class.java).apply {
            putExtra(WidgetPinReceiver.EXTRA_FOLDER_ID, folderId)
        }
        val successCallback = PendingIntent.getBroadcast(
            context,
            folderId.hashCode(),
            callbackIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
        manager.requestPinAppWidget(provider, null, successCallback)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun WatchlistTopBar(
    onAddFolder: () -> Unit,
    scrollBehavior: androidx.compose.material3.TopAppBarScrollBehavior,
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
            scrolledContainerColor = ScreenBackground,
        )
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun WatchlistMainContent(
    lazyPagingItems: LazyPagingItems<Coin>,
    uiState: WatchlistUiState,
    actions: WatchlistActions,
) {
    val listState = rememberLazyListState()
    val isRefreshing = remember(lazyPagingItems.loadState.refresh, lazyPagingItems.itemCount) {
        lazyPagingItems.loadState.refresh is LoadState.Loading && lazyPagingItems.itemCount > 0
    }

    when (val refreshState = lazyPagingItems.loadState.refresh) {
        is LoadState.Loading -> {
            if (lazyPagingItems.itemCount == 0) LoadingView()
        }

        is LoadState.Error -> {
            ErrorView(
                error = refreshState.error,
                onRetry = { lazyPagingItems.refresh() },
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
                    actions = actions,
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
    actions: WatchlistActions,
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
                contentType = { "folder" },
            ) { folder ->
                FolderItem(
                    modifier = Modifier.animateItem(),
                    folder = folder,
                    onRename = { actions.onRenameFolder(folder) },
                    onDelete = { actions.onEvent(WatchlistUiEvent.DeleteFolder(folder.id)) },
                    onRemoveCoin = { coinId -> actions.onRemoveCoinFromFolder(coinId, folder) },
                    onAddToHomeScreen = { actions.onAddToHomeScreen(folder.id) },
                    priceProvider = { symbol ->
                        val price = actions.priceProvider(symbol) ?: 0.0
                        actions.formatPrice(price)
                    },
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
            contentType = lazyPagingItems.itemContentType { "coin" },
        ) { index ->
            val coin = lazyPagingItems[index]
            coin?.let {
                CoinListItem(
                    modifier = Modifier.animateItem(),
                    coin = it,
                    priceProvider = {
                        val price = actions.priceProvider(it.symbol) ?: it.price
                        actions.formatPrice(price)
                    },
                    isFoldered = it.symbol in uiState.coinIdsInFolders,
                    onAddToFolder = { actions.onCoinForFolder(it.symbol) },
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
