package com.abhay.crypto.core.network

import com.abhay.crypto.core.network.dto.TickerPriceDto
import retrofit2.http.GET

interface BinanceApi {

    @GET("api/v3/ticker/price")
    suspend fun getTickerPrices(): List<TickerPriceDto>
}
