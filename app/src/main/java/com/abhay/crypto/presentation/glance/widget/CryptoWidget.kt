package com.abhay.crypto.presentation.glance.widget

import android.content.Context
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.glance.GlanceId
import androidx.glance.GlanceTheme
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.provideContent
import androidx.glance.currentState
import androidx.glance.state.GlanceStateDefinition
import androidx.glance.state.PreferencesGlanceStateDefinition
import com.abhay.crypto.R
import com.abhay.crypto.domain.NetworkMonitor
import com.abhay.crypto.domain.model.Coin
import com.abhay.crypto.domain.repository.CoinRepository
import com.abhay.crypto.domain.repository.FolderRepository
import com.abhay.crypto.domain.usecase.FormatPriceUseCase
import dagger.hilt.EntryPoint
import dagger.hilt.EntryPoints
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

class CryptoWidget : GlanceAppWidget() {

    override val stateDefinition: GlanceStateDefinition<*> = PreferencesGlanceStateDefinition

    companion object {
        val FOLDER_ID_KEY = stringPreferencesKey("folder_id")
    }

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface CryptoWidgetEntryPoint {
        fun coinRepository(): CoinRepository
        fun folderRepository(): FolderRepository
        fun formatPriceUseCase(): FormatPriceUseCase
        fun networkMonitor(): NetworkMonitor
    }

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val entryPoint =
            EntryPoints.get(context.applicationContext, CryptoWidgetEntryPoint::class.java)
        val folderRepository = entryPoint.folderRepository()
        val coinRepository = entryPoint.coinRepository()
        val formatPriceUseCase = entryPoint.formatPriceUseCase()
        val networkMonitor = entryPoint.networkMonitor()

        provideContent {
            val prefs = currentState<Preferences>()
            val folderId = prefs[FOLDER_ID_KEY]

            // Observe network status
            val isOnline by networkMonitor.isAvailable.collectAsState(initial = true)

            // Collect folders safely
            val foldersState =
                remember { folderRepository.getFolders() }.collectAsState(initial = emptyList())
            val folders = foldersState.value
            val folder = folders.find { it.id == folderId } ?: folders.firstOrNull()

            // Observe live prices
            val livePrices by remember { coinRepository.observeLivePrices() }.collectAsState(initial = emptyMap())

            // Load coins for this folder
            val coinsState = produceState<List<Coin>>(initialValue = emptyList(), key1 = folder) {
                value = folder?.let { coinRepository.getCoinsByIds(it.coinIds) } ?: emptyList()
            }

            val displayCoins = remember(coinsState.value, livePrices) {
                coinsState.value.map { coin ->
                    val livePrice = livePrices[coin.symbol]
                    if (livePrice != null) coin.copy(price = livePrice) else coin
                }
            }

            GlanceTheme {
                CryptoWidgetContent(
                    folderName = folder?.name ?: context.getString(R.string.watchlist),
                    coins = displayCoins,
                    isOnline = isOnline,
                    formatPrice = { formatPriceUseCase(it) }
                )
            }
        }
    }
}
