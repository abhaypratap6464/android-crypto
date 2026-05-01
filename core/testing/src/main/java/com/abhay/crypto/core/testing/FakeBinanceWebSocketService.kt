package com.abhay.crypto.core.testing

import com.abhay.crypto.core.network.BinanceWebSocketService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class FakeBinanceWebSocketService : BinanceWebSocketService {
    private val _prices = MutableStateFlow<Map<String, Double>>(emptyMap())
    fun setPrices(prices: Map<String, Double>) {
        _prices.value = prices
    }

    override fun observePrices(symbols: Flow<Set<String>>): Flow<Map<String, Double>> = _prices
}
