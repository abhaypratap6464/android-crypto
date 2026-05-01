package com.abhay.crypto.core.testing

import androidx.paging.PagingData
import com.abhay.crypto.core.domain.model.Coin
import com.abhay.crypto.core.domain.repository.CoinRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class FakeCoinRepository : CoinRepository {
    private val pagedCoins = MutableStateFlow<PagingData<Coin>>(PagingData.empty())
    private val livePrices = MutableStateFlow<Map<String, Double>>(emptyMap())

    fun setPagedCoins(data: PagingData<Coin>) {
        pagedCoins.value = data
    }

    fun setLivePrices(prices: Map<String, Double>) {
        livePrices.value = prices
    }

    override fun getPagedCoins(): Flow<PagingData<Coin>> = pagedCoins

    override fun observeLivePrices(symbols: Flow<Set<String>>): Flow<Map<String, Double>> =
        livePrices

    override suspend fun getCoinsByIds(ids: List<String>): List<Coin> = emptyList()
}
