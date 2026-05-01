package com.abhay.crypto.core.network

import kotlinx.coroutines.flow.Flow

interface BinanceWebSocketService {
    /**
     * Observes real-time prices for the given [symbols].
     * Subscribes/Unsubscribes dynamically as the [symbols] flow changes.
     */
    fun observePrices(symbols: Flow<Set<String>>): Flow<Map<String, Double>>
}
