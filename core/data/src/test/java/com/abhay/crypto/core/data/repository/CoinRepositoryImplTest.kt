package com.abhay.crypto.core.data.repository

import app.cash.turbine.test
import com.abhay.crypto.core.network.dto.TickerPriceDto
import com.abhay.crypto.core.testing.FakeBinanceApi
import com.abhay.crypto.core.testing.FakeBinanceWebSocketService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CoinRepositoryImplTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var api: FakeBinanceApi
    private lateinit var webSocketService: FakeBinanceWebSocketService
    private lateinit var repository: CoinRepositoryImpl

    @Before
    fun setup() {
        api = FakeBinanceApi()
        webSocketService = FakeBinanceWebSocketService()
        repository = CoinRepositoryImpl(api, webSocketService, CoroutineScope(testDispatcher))
    }

    @Test
    fun `observeLivePrices combines rest price cache and websocket prices`() =
        runTest(testDispatcher) {
            repository.observeLivePrices().test {
                assertEquals(emptyMap<String, Double>(), awaitItem())

                val wsPrices = mapOf("BTCUSDT" to 50000.0)
                webSocketService.emitPrices(wsPrices)
                assertEquals(wsPrices, awaitItem())
            }
        }

    @Test
    fun `getCoinsByIds returns mapped coins from api`() = runTest(testDispatcher) {
        val dtos = listOf(
            TickerPriceDto("BTCUSDT", "50000.00"),
            TickerPriceDto("ETHUSDT", "3000.00")
        )
        api.setPrices(dtos)

        val result = repository.getCoinsByIds(listOf("BTCUSDT"))

        assertEquals(1, result.size)
        assertEquals("BTC", result.first().baseAsset)
        assertEquals(50000.0, result.first().price, 0.0)
    }
}
