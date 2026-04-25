package com.abhay.crypto.core.network

import android.util.Log
import com.abhay.crypto.core.domain.Constants
import com.abhay.crypto.core.network.dto.MiniTickerDto
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
class BinanceWebSocketServiceImpl @Inject constructor(
    private val okHttpClient: OkHttpClient,
    private val json: Json,
) : BinanceWebSocketService {
    private val streamUrl = "wss://stream.binance.com:9443/ws/!miniTicker@arr"

    companion object {
        private const val TAG = "BinanceWS"
        private const val NORMAL_CLOSURE_STATUS = 1000
    }

    override fun observePrices(): Flow<Map<String, Double>> = callbackFlow {
        val producer = this
        val request = Request.Builder().url(streamUrl).build()

        val listener = object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                Log.d(TAG, "WebSocket Opened")
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                runCatching {
                    val tickers = json.decodeFromString<List<MiniTickerDto>>(text)
                    val prices = tickers.asSequence()
                        .filter { it.symbol.endsWith("USDT") }
                        .associateBy({ it.symbol }, { it.closePrice.toDoubleOrNull() ?: 0.0 })

                    if (prices.isNotEmpty()) {
                        producer.trySend(prices)
                    }
                }.onFailure { e ->
                    Log.e(TAG, "Error parsing WebSocket message", e)
                }
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                Log.e(TAG, "WebSocket Failure: ${t.message}", t)
                producer.close(t)
            }

            override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                Log.d(TAG, "WebSocket Closed: $reason")
                producer.close()
            }
        }

        val webSocket = okHttpClient.newWebSocket(request, listener)

        awaitClose {
            Log.d(TAG, "Closing WebSocket")
            webSocket.close(NORMAL_CLOSURE_STATUS, "Flow closed")
        }
    }
        .retry { e ->
            Log.d(TAG, "Retrying connection after error: ${e.message}")
            delay(Constants.WEBSOCKET_RETRY_DELAY_MS)
            true
        }
        .flowOn(Dispatchers.IO)
}
