package com.abhay.crypto.core.testing

import com.abhay.crypto.core.network.BinanceWebSocketService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class FakeBinanceWebSocketService : BinanceWebSocketService {

    private val tickerFlow = MutableStateFlow<Map<String, Double>>(emptyMap())

    override fun observePrices(): Flow<Map<String, Double>> = tickerFlow

    suspend fun emitPrices(prices: Map<String, Double>) {
        tickerFlow.emit(prices)
    }
}
