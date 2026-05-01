package com.abhay.crypto.core.domain.repository

import androidx.paging.PagingData
import com.abhay.crypto.core.domain.model.Coin
import kotlinx.coroutines.flow.Flow

interface CoinRepository {
    fun getPagedCoins(): Flow<PagingData<Coin>>
    fun observeLivePrices(symbols: Flow<Set<String>>): Flow<Map<String, Double>>
    suspend fun getCoinsByIds(ids: List<String>): List<Coin>
}
