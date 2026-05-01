package com.abhay.crypto.feature.widget.impl

import android.content.Context
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.glance.GlanceId
import androidx.glance.GlanceTheme
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.provideContent
import androidx.glance.currentState
import androidx.glance.state.GlanceStateDefinition
import androidx.glance.state.PreferencesGlanceStateDefinition
import com.abhay.crypto.core.domain.NetworkMonitor
import com.abhay.crypto.core.domain.repository.CoinRepository
import com.abhay.crypto.core.domain.repository.FolderRepository
import com.abhay.crypto.core.domain.usecase.FormatPriceUseCase
import com.abhay.crypto.core.ui.R
import dagger.hilt.EntryPoint
import dagger.hilt.EntryPoints
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.MutableStateFlow

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

            val isOnline by networkMonitor.isAvailable.collectAsState(initial = true)

            // No remember{} — Glance re-creates composition on each update cycle so
            // remember() does not persist state the way it does in Activity-hosted Compose.
            val folders by folderRepository.getFolders().collectAsState(initial = emptyList())
            val folder = folders.find { it.id == folderId } ?: folders.firstOrNull()

            val symbols = folder?.coinIds?.toSet() ?: emptySet()
            val livePrices by coinRepository.observeLivePrices(MutableStateFlow(symbols))
                .collectAsState(initial = emptyMap())

            val coins by produceState(initialValue = emptyList(), key1 = folder) {
                value = folder?.let { coinRepository.getCoinsByIds(it.coinIds) } ?: emptyList()
            }

            // Merge REST-fetched coins with latest WebSocket prices inline — no remember needed.
            val displayCoins = coins.map { coin ->
                coin.copy(price = livePrices[coin.symbol] ?: coin.price)
            }

            GlanceTheme {
                CryptoWidgetContent(
                    folderName = folder?.name ?: context.getString(R.string.watchlist),
                    coins = displayCoins,
                    isOnline = isOnline,
                    formatPrice = formatPriceUseCase::invoke,
                )
            }
        }
    }
}
