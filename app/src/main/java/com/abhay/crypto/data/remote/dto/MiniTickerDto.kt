package com.abhay.crypto.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MiniTickerDto(
    @SerialName("s") val symbol: String,
    @SerialName("c") val closePrice: String,
)
