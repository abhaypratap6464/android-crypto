package com.abhay.crypto.data.repository

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
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

private const val PAGE_SIZE = 10
private const val PREFETCH_DISTANCE = 2

class CoinRepositoryImpl @Inject constructor(
    private val api: BinanceApi,
    private val webSocketService: BinanceWebSocketService,
) : CoinRepository {

    override fun getPagedCoins(): Flow<PagingData<Coin>> =
        Pager(
            config = PagingConfig(
                pageSize = PAGE_SIZE,
                prefetchDistance = PREFETCH_DISTANCE,
                enablePlaceholders = false,
            ),
            pagingSourceFactory = { CoinPagingSource(api) },
        ).flow.flowOn(Dispatchers.IO)

    override fun observeLivePrices(): Flow<Map<String, Double>> =
        webSocketService.observePrices()
}
