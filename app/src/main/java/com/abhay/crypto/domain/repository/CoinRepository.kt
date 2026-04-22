package com.abhay.crypto.domain.repository

import androidx.paging.PagingData
import com.abhay.crypto.domain.model.Coin
import kotlinx.coroutines.flow.Flow

interface CoinRepository {
    fun getPagedCoins(): Flow<PagingData<Coin>>
    fun observeLivePrices(): Flow<Map<String, Double>>
    suspend fun getCoinsByIds(ids: List<String>): List<Coin>
}
