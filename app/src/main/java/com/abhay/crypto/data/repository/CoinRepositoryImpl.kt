package com.abhay.crypto.data.repository

import android.util.Log
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.abhay.crypto.data.paging.CoinPagingSource
import com.abhay.crypto.data.remote.BinanceApi
import com.abhay.crypto.data.remote.BinanceWebSocketService
import com.abhay.crypto.domain.model.Coin
import com.abhay.crypto.domain.repository.CoinRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import javax.inject.Inject

private const val PAGE_SIZE = 10
private const val PREFETCH_DISTANCE = 2

class CoinRepositoryImpl @Inject constructor(
    private val api: BinanceApi,
    private val webSocketService: BinanceWebSocketService,
) : CoinRepository {

    // Populated by CoinPagingSource on first load. Acts as a fallback so every coin
    // has a price before the WebSocket sends its first tick.
    private val restPriceCache = MutableStateFlow<Map<String, Double>>(emptyMap())

    override fun getPagedCoins(): Flow<PagingData<Coin>> =
        Pager(
            config = PagingConfig(
                pageSize = PAGE_SIZE,
                prefetchDistance = PREFETCH_DISTANCE,
                enablePlaceholders = false,
            ),
            pagingSourceFactory = { CoinPagingSource(api, restPriceCache) },
        ).flow.flowOn(Dispatchers.IO)

    override fun observeLivePrices(): Flow<Map<String, Double>> =
        // REST prices are the baseline; WebSocket values override them as they arrive.
        combine(restPriceCache, webSocketService.observePrices()) { rest, live ->
            rest + live
        }

    override suspend fun getCoinsByIds(ids: List<String>): List<Coin> =
        withContext(Dispatchers.IO) {
            runCatching {
                api.getTickerPrices()
                    .filter { it.symbol in ids }
                    .map { dto ->
                        Coin(
                            symbol = dto.symbol,
                            baseAsset = dto.symbol.removeSuffix("USDT"),
                            price = dto.price.toDoubleOrNull() ?: 0.0,
                        )
                    }
            }.getOrElse { e ->
                Log.e("CoinRepository", "Error fetching coins by ids", e)
                emptyList()
            }
        }
}
