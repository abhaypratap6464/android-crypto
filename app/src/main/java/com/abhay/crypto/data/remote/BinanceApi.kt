package com.abhay.crypto.data.remote

import com.abhay.crypto.data.remote.dto.TickerPriceDto
import retrofit2.http.GET

interface BinanceApi {

    @GET("api/v3/ticker/price")
    suspend fun getTickerPrices(): List<TickerPriceDto>
}
