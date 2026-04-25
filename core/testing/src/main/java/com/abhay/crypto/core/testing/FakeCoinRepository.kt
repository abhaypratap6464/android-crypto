package com.abhay.crypto.core.testing

import androidx.paging.PagingData
import com.abhay.crypto.core.domain.model.Coin
import com.abhay.crypto.core.domain.repository.CoinRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf

class FakeCoinRepository : CoinRepository {

    private val livePricesFlow = MutableStateFlow<Map<String, Double>>(emptyMap())
    private var coins = listOf<Coin>()

    override fun getPagedCoins(): Flow<PagingData<Coin>> = flowOf(PagingData.from(coins))

    override fun observeLivePrices(): Flow<Map<String, Double>> = livePricesFlow

    override suspend fun getCoinsByIds(ids: List<String>): List<Coin> {
        return coins.filter { it.symbol in ids }
    }

    fun setCoins(coins: List<Coin>) {
        this.coins = coins
    }

    fun setLivePrices(prices: Map<String, Double>) {
        livePricesFlow.value = prices
    }
}
