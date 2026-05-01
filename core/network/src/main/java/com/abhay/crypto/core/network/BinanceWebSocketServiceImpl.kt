package com.abhay.crypto.core.network

import android.util.Log
import com.abhay.crypto.core.domain.Constants
import com.abhay.crypto.core.network.dto.BinanceSubscriptionMessage
import com.abhay.crypto.core.network.dto.MiniTickerDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.retry
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import java.util.concurrent.atomic.AtomicInteger
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BinanceWebSocketServiceImpl @Inject constructor(
    private val okHttpClient: OkHttpClient,
    private val json: Json,
) : BinanceWebSocketService {
    private val baseUrl = "wss://stream.binance.com:9443/ws"
    private val requestId = AtomicInteger(1)

    companion object {
        private const val TAG = "BinanceWS"
        private const val NORMAL_CLOSURE_STATUS = 1000
    }

    override fun observePrices(symbols: Flow<Set<String>>): Flow<Map<String, Double>> =
        callbackFlow {
        val producer = this
            val currentSubscriptions = mutableSetOf<String>()
            val request = Request.Builder().url(baseUrl).build()

        val listener = object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                Log.d(TAG, "WebSocket Opened")
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                runCatching {
                    // Try parsing as single ticker first (individual stream format)
                    val ticker = json.decodeFromString<MiniTickerDto>(text)
                    if (ticker.symbol.endsWith("USDT")) {
                        producer.trySend(
                            mapOf(
                                ticker.symbol to (ticker.closePrice.toDoubleOrNull() ?: 0.0)
                            )
                        )
                    }
                }.onFailure {
                    // It might be a control message response or something else, ignore if parsing fails
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

            // Handle dynamic subscriptions
            symbols.onEach { requestedSymbols ->
                val requestedStreams =
                    requestedSymbols.map { "${it.lowercase()}@miniTicker" }.toSet()

                val toUnsubscribe = currentSubscriptions - requestedStreams
                val toSubscribe = requestedStreams - currentSubscriptions

                if (toUnsubscribe.isNotEmpty()) {
                    val msg = BinanceSubscriptionMessage(
                        method = BinanceSubscriptionMessage.UNSUBSCRIBE,
                        params = toUnsubscribe.toList(),
                        id = requestId.incrementAndGet()
                    )
                    webSocket.send(json.encodeToString(msg))
                    currentSubscriptions.removeAll(toUnsubscribe)
                }

                if (toSubscribe.isNotEmpty()) {
                    val msg = BinanceSubscriptionMessage(
                        method = BinanceSubscriptionMessage.SUBSCRIBE,
                        params = toSubscribe.toList(),
                        id = requestId.incrementAndGet()
                    )
                    webSocket.send(json.encodeToString(msg))
                    currentSubscriptions.addAll(toSubscribe)
                }
            }.launchIn(this)

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
