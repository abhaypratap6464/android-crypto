package com.abhay.crypto.data.remote

import com.abhay.crypto.data.remote.dto.MiniTickerDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.retry
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BinanceWebSocketService @Inject constructor(
    private val okHttpClient: OkHttpClient,
    private val json: Json,
) {
    private val streamUrl = "wss://stream.binance.com:9443/ws/!miniTicker@arr"

    companion object {
        private const val NORMAL_CLOSURE_STATUS = 1000
        private const val RETRY_DELAY_MS = 5_000L
    }

    fun observePrices(): Flow<Map<String, Double>> = callbackFlow {
        val producer = this
        val request = Request.Builder().url(streamUrl).build()

        val listener = object : WebSocketListener() {
            override fun onMessage(webSocket: WebSocket, text: String) {
                runCatching {
                    val tickers = json.decodeFromString<List<MiniTickerDto>>(text)
                    val prices = tickers
                        .filter { it.symbol.endsWith("USDT") }
                        .associate { it.symbol to (it.closePrice.toDoubleOrNull() ?: 0.0) }
                    producer.trySend(prices)
                }
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                producer.close(t)
            }

            override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                producer.close()
            }
        }

        val webSocket = okHttpClient.newWebSocket(request, listener)
        awaitClose { webSocket.close(NORMAL_CLOSURE_STATUS, null) }
    }
        .retry { delay(RETRY_DELAY_MS); true }
        .flowOn(Dispatchers.IO)
}
