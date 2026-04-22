package com.abhay.crypto.presentation.watchlist

import androidx.paging.PagingData
import com.abhay.crypto.domain.model.BookmarkFolder
import com.abhay.crypto.domain.usecase.FormatPriceUseCase
import com.abhay.crypto.domain.usecase.GetPagedCoinsUseCase
import com.abhay.crypto.domain.usecase.ObserveLivePricesUseCase
import com.abhay.crypto.domain.usecase.ObserveNetworkUseCase
import com.abhay.crypto.domain.usecase.folder.AddBookmarkToFolderUseCase
import com.abhay.crypto.domain.usecase.folder.CreateFolderUseCase
import com.abhay.crypto.domain.usecase.folder.DeleteFolderUseCase
import com.abhay.crypto.domain.usecase.folder.GetFoldersUseCase
import com.abhay.crypto.domain.usecase.folder.RemoveBookmarkFromFolderUseCase
import com.abhay.crypto.domain.usecase.folder.RenameFolderUseCase
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class WatchlistViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    private val getPagedCoins: GetPagedCoinsUseCase = mockk()
    private val observeLivePrices: ObserveLivePricesUseCase = mockk()
    private val observeNetwork: ObserveNetworkUseCase = mockk()
    private val getFolders: GetFoldersUseCase = mockk()
    private val formatPriceUseCase: FormatPriceUseCase = mockk()
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

        viewModel = WatchlistViewModel(
            getPagedCoins, observeLivePrices, observeNetwork, getFolders,
            formatPriceUseCase, createFolder, renameFolder, deleteFolder,
            addBookmarkToFolder, removeBookmarkFromFolder,
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state has network available and empty folders`() = runTest {
        val state = viewModel.uiState.value
        assertTrue(state.isNetworkAvailable)
        assertTrue(state.folders.isEmpty())
        assertTrue(state.coinIdsInFolders.isEmpty())
    }

    @Test
    fun `uiState reflects network offline`() = runTest {
        coEvery { observeNetwork() } returns flowOf(false)

        val offlineViewModel = WatchlistViewModel(
            getPagedCoins, observeLivePrices, observeNetwork, getFolders,
            formatPriceUseCase, createFolder, renameFolder, deleteFolder,
            addBookmarkToFolder, removeBookmarkFromFolder,
        )

        // WhileSubscribed won't collect until there's a subscriber — start one first.
        val job = launch(UnconfinedTestDispatcher(testScheduler)) {
            offlineViewModel.uiState.collect {}
        }

        assertFalse(offlineViewModel.uiState.value.isNetworkAvailable)
        job.cancel()
    }

    @Test
    fun `uiState reflects folders from repository`() = runTest {
        val folder = BookmarkFolder(id = "1", name = "DeFi", coinIds = listOf("ETHUSDT"))
        coEvery { getFolders() } returns flowOf(listOf(folder))

        val vmWithFolder = WatchlistViewModel(
            getPagedCoins, observeLivePrices, observeNetwork, getFolders,
            formatPriceUseCase, createFolder, renameFolder, deleteFolder,
            addBookmarkToFolder, removeBookmarkFromFolder,
        )

        val job = launch(UnconfinedTestDispatcher(testScheduler)) {
            vmWithFolder.uiState.collect {}
        }

        val state = vmWithFolder.uiState.value
        assertEquals(1, state.folders.size)
        assertEquals("DeFi", state.folders.first().name)
        assertTrue("ETHUSDT" in state.coinIdsInFolders)
        job.cancel()
    }

    @Test
    fun `formatPrice delegates to use case`() {
        every { formatPriceUseCase(45000.0) } returns "$45000.00"
        assertEquals("$45000.00", viewModel.formatPrice(45000.0))
    }

    @Test
    fun `create folder event calls use case`() = runTest {
        val name = "Favorites"
        val coinId = "BTCUSDT"
        coEvery { createFolder(name, coinId) } just Runs

        viewModel.onEvent(WatchlistUiEvent.CreateFolder(name, coinId))

        coVerify { createFolder(name, coinId) }
    }

    @Test
    fun `delete folder event calls use case`() = runTest {
        coEvery { deleteFolder("folder-1") } just Runs

        viewModel.onEvent(WatchlistUiEvent.DeleteFolder("folder-1"))

        coVerify { deleteFolder("folder-1") }
    }

    @Test
    fun `rename folder event calls use case`() = runTest {
        coEvery { renameFolder("folder-1", "New Name") } just Runs

        viewModel.onEvent(WatchlistUiEvent.RenameFolder("folder-1", "New Name"))

        coVerify { renameFolder("folder-1", "New Name") }
    }

    @Test
    fun `add bookmark adds coin when not already in folder`() = runTest {
        val folder = BookmarkFolder(id = "folder-1", name = "DeFi", coinIds = emptyList())
        coEvery { getFolders() } returns flowOf(listOf(folder))
        coEvery { addBookmarkToFolder("folder-1", "BTCUSDT") } just Runs

        val vm = WatchlistViewModel(
            getPagedCoins, observeLivePrices, observeNetwork, getFolders,
            formatPriceUseCase, createFolder, renameFolder, deleteFolder,
            addBookmarkToFolder, removeBookmarkFromFolder,
        )

        vm.onEvent(WatchlistUiEvent.AddBookmarkToFolder("folder-1", "BTCUSDT"))

        coVerify { addBookmarkToFolder("folder-1", "BTCUSDT") }
    }

    @Test
    fun `add bookmark removes coin when already in folder`() = runTest {
        val folder = BookmarkFolder(id = "folder-1", name = "DeFi", coinIds = listOf("BTCUSDT"))
        coEvery { getFolders() } returns flowOf(listOf(folder))
        coEvery { removeBookmarkFromFolder("folder-1", "BTCUSDT") } just Runs

        val vm = WatchlistViewModel(
            getPagedCoins, observeLivePrices, observeNetwork, getFolders,
            formatPriceUseCase, createFolder, renameFolder, deleteFolder,
            addBookmarkToFolder, removeBookmarkFromFolder,
        )

        // Subscribing triggers WhileSubscribed so uiState.value.folders is populated
        // before onEvent reads it to decide add vs remove.
        val job = launch(UnconfinedTestDispatcher(testScheduler)) { vm.uiState.collect {} }

        vm.onEvent(WatchlistUiEvent.AddBookmarkToFolder("folder-1", "BTCUSDT"))

        coVerify { removeBookmarkFromFolder("folder-1", "BTCUSDT") }
        job.cancel()
    }
}
