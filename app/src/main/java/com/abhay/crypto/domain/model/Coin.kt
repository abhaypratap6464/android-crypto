package com.abhay.crypto.domain.model

data class Coin(
    val symbol: String,
    val baseAsset: String,
    val price: Double,
)
