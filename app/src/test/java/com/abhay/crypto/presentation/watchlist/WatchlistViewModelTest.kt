package com.abhay.crypto.presentation.watchlist

import androidx.paging.PagingData
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
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.just
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class WatchlistViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    private val getPagedCoins: GetPagedCoinsUseCase = mockk()
    private val observeLivePrices: ObserveLivePricesUseCase = mockk()
    private val observeNetwork: ObserveNetworkUseCase = mockk()
    private val getFolders: GetFoldersUseCase = mockk()
    private val getWatchListed: GetWatchListedUseCase = mockk()
    private val formatPrice: FormatPriceUseCase = mockk()
    private val toggleWatchList: ToggleWatchListUseCase = mockk()
    private val createFolder: CreateFolderUseCase = mockk()
    private val renameFolder: RenameFolderUseCase = mockk()
    private val deleteFolder: DeleteFolderUseCase = mockk()
    private val addBookmarkToFolder: AddBookmarkToFolderUseCase = mockk()
    private val removeBookmarkFromFolder: RemoveBookmarkFromFolderUseCase = mockk()

    private lateinit var viewModel: WatchlistViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        coEvery { getPagedCoins() } returns flowOf(PagingData.empty())
        coEvery { observeLivePrices() } returns flowOf(emptyMap())
        coEvery { observeNetwork() } returns flowOf(true)
        coEvery { getFolders() } returns flowOf(emptyList())
        coEvery { getWatchListed() } returns flowOf(emptySet())

        viewModel = WatchlistViewModel(
            getPagedCoins, observeLivePrices, observeNetwork, getFolders, getWatchListed,
            formatPrice, toggleWatchList, createFolder, renameFolder, deleteFolder,
            addBookmarkToFolder, removeBookmarkFromFolder
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is correct`() = runTest {
        val state = viewModel.uiState.value
        assert(state.isNetworkAvailable)
        assert(state.folders.isEmpty())
        assert(state.watchListed.isEmpty())
    }

    @Test
    fun `toggle watchlist event calls use case`() = runTest {
        val symbol = "BTCUSDT"
        coEvery { toggleWatchList(symbol) } just Runs

        viewModel.onEvent(WatchlistUiEvent.ToggleWatchList(symbol))

        coVerify { toggleWatchList(symbol) }
    }

    @Test
    fun `create folder event calls use case`() = runTest {
        val name = "Favorites"
        val coinId = "BTCUSDT"
        coEvery { createFolder(name, coinId) } just Runs

        viewModel.onEvent(WatchlistUiEvent.CreateFolder(name, coinId))

        coVerify { createFolder(name, coinId) }
    }
}
