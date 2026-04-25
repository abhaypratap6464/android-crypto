package com.abhay.crypto.core.network

import kotlinx.coroutines.flow.Flow

interface BinanceWebSocketService {
    fun observePrices(): Flow<Map<String, Double>>
}
