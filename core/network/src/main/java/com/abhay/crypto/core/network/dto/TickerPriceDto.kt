package com.abhay.crypto.core.network.dto

import kotlinx.serialization.Serializable

@Serializable
data class TickerPriceDto(
    val symbol: String,
    val price: String,
)
