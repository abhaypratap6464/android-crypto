package com.abhay.crypto.core.testing

import com.abhay.crypto.core.network.BinanceApi
import com.abhay.crypto.core.network.dto.TickerPriceDto

class FakeBinanceApi : BinanceApi {

    private var prices = listOf<TickerPriceDto>()
    var throwError = false

    override suspend fun getTickerPrices(): List<TickerPriceDto> {
        if (throwError) throw java.io.IOException("Network error")
        return prices
    }

    fun setPrices(prices: List<TickerPriceDto>) {
        this.prices = prices
    }
}
