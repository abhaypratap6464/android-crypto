package com.abhay.crypto.feature.watchlist.impl

import com.abhay.crypto.core.domain.model.BookmarkFolder
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
import com.abhay.crypto.core.testing.FakeCoinRepository
import com.abhay.crypto.core.testing.FakeFolderRepository
import com.abhay.crypto.core.testing.FakeNetworkMonitor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
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

    private lateinit var folderRepository: FakeFolderRepository
    private lateinit var coinRepository: FakeCoinRepository
    private lateinit var networkMonitor: FakeNetworkMonitor

    private lateinit var viewModel: WatchlistViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        folderRepository = FakeFolderRepository()
        coinRepository = FakeCoinRepository()
        networkMonitor = FakeNetworkMonitor()

        viewModel = WatchlistViewModel(
            getPagedCoins = GetPagedCoinsUseCase(coinRepository),
            observeLivePrices = ObserveLivePricesUseCase(coinRepository),
            observeNetwork = ObserveNetworkUseCase(networkMonitor),
            getFolders = GetFoldersUseCase(folderRepository),
            formatPriceUseCase = FormatPriceUseCase(),
            createFolder = CreateFolderUseCase(folderRepository),
            renameFolder = RenameFolderUseCase(folderRepository),
            deleteFolder = DeleteFolderUseCase(folderRepository),
            addBookmarkToFolder = AddBookmarkToFolderUseCase(folderRepository),
            removeBookmarkFromFolder = RemoveBookmarkFromFolderUseCase(folderRepository),
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
        networkMonitor.setAvailable(false)

        // WhileSubscribed won't collect until there's a subscriber — start one first.
        val job = launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect {}
        }

        assertFalse(viewModel.uiState.value.isNetworkAvailable)
        job.cancel()
    }

    @Test
    fun `uiState reflects folders from repository`() = runTest {
        val folder = BookmarkFolder(id = "1", name = "DeFi", coinIds = listOf("ETHUSDT"))
        folderRepository.setFolders(listOf(folder))

        val job = launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect {}
        }

        val state = viewModel.uiState.value
        assertEquals(1, state.folders.size)
        assertEquals("DeFi", state.folders.first().name)
        assertTrue("ETHUSDT" in state.coinIdsInFolders)
        job.cancel()
    }

    @Test
    fun `formatPrice returns correctly formatted string`() {
        assertEquals("$45,000.00", viewModel.formatPrice(45000.0))
    }

    @Test
    fun `create folder event updates repository`() = runTest {
        val name = "Favorites"
        val coinId = "BTCUSDT"

        viewModel.onEvent(WatchlistUiEvent.CreateFolder(name, coinId))

        val folders = folderRepository.getFolders().first()
        assertEquals(1, folders.size)
        assertEquals(name, folders.first().name)
        assertTrue(coinId in folders.first().coinIds)
    }

    @Test
    fun `delete folder event updates repository`() = runTest {
        folderRepository.createFolder("To Delete", null)
        val folderId = folderRepository.getFolders().first().first().id

        viewModel.onEvent(WatchlistUiEvent.DeleteFolder(folderId))

        assertTrue(folderRepository.getFolders().first().isEmpty())
    }

    @Test
    fun `rename folder event updates repository`() = runTest {
        folderRepository.createFolder("Old Name", null)
        val folderId = folderRepository.getFolders().first().first().id

        viewModel.onEvent(WatchlistUiEvent.RenameFolder(folderId, "New Name"))

        assertEquals("New Name", folderRepository.getFolders().first().first().name)
    }

    @Test
    fun `add bookmark adds coin when not already in folder`() = runTest {
        folderRepository.createFolder("DeFi", null)
        val folderId = folderRepository.getFolders().first().first().id

        viewModel.onEvent(WatchlistUiEvent.AddBookmarkToFolder(folderId, "BTCUSDT"))

        assertTrue("BTCUSDT" in folderRepository.getFolders().first().first().coinIds)
    }

    @Test
    fun `add bookmark removes coin when already in folder`() = runTest {
        folderRepository.createFolder("DeFi", "BTCUSDT")
        val folderId = folderRepository.getFolders().first().first().id

        // Need to collect state for ViewModel to read updated folders list before decision
        val job = launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.uiState.collect {} }

        viewModel.onEvent(WatchlistUiEvent.AddBookmarkToFolder(folderId, "BTCUSDT"))

        assertFalse("BTCUSDT" in folderRepository.getFolders().first().first().coinIds)
        job.cancel()
    }
}
