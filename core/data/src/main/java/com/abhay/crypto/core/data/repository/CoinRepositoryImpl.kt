package com.abhay.crypto.core.data.repository

import android.util.Log
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.abhay.crypto.core.data.di.ApplicationScope
import com.abhay.crypto.core.data.paging.CoinPagingSource
import com.abhay.crypto.core.domain.Constants
import com.abhay.crypto.core.domain.model.Coin
import com.abhay.crypto.core.domain.repository.CoinRepository
import com.abhay.crypto.core.network.BinanceApi
import com.abhay.crypto.core.network.BinanceWebSocketService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.withContext
import javax.inject.Inject

class CoinRepositoryImpl @Inject constructor(
    private val api: BinanceApi,
    webSocketService: BinanceWebSocketService,
    @param:ApplicationScope private val externalScope: CoroutineScope,
) : CoinRepository {

    // Populated by CoinPagingSource on first load. Acts as a fallback so every coin
    // has a price before the WebSocket sends its first tick.
    private val restPriceCache = MutableStateFlow<Map<String, Double>>(emptyMap())

    // Shared so all subscribers (ViewModel + Glance widget) reuse the same WebSocket
    // connection rather than each opening their own.
    private val sharedLivePrices: Flow<Map<String, Double>> =
        combine(restPriceCache, webSocketService.observePrices()) { rest, live ->
            rest + live
        }.shareIn(
            scope = externalScope,
            started = SharingStarted.WhileSubscribed(Constants.WEBSOCKET_STOP_TIMEOUT_MS),
            replay = 1,
        )

    override fun getPagedCoins(): Flow<PagingData<Coin>> =
        Pager(
            config = PagingConfig(
                pageSize = Constants.PAGE_SIZE,
                prefetchDistance = Constants.PREFETCH_DISTANCE,
                enablePlaceholders = false,
            ),
            pagingSourceFactory = {
                CoinPagingSource(api) { prices -> restPriceCache.value = prices }
            }
        ).flow.flowOn(Dispatchers.IO)

    override fun observeLivePrices(): Flow<Map<String, Double>> = sharedLivePrices

    override suspend fun getCoinsByIds(ids: List<String>): List<Coin> =
        withContext(Dispatchers.IO) {
            runCatching {
                api.getTickerPrices()
                    .asSequence()
                    .filter { it.symbol in ids }
                    .map { dto ->
                        Coin(
                            symbol = dto.symbol,
                            baseAsset = dto.symbol.removeSuffix("USDT"),
                            price = dto.price.toDoubleOrNull() ?: 0.0,
                        )
                    }
                    .toList()
            }.getOrElse { e ->
                Log.e("CoinRepository", "Error fetching coins by ids", e)
                emptyList()
            }
        }
}
