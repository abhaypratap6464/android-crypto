package com.abhay.crypto.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class TickerPriceDto(
    val symbol: String,
    val price: String,
)
