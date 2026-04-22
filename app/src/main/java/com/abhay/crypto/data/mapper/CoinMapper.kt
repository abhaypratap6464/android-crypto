package com.abhay.crypto.data.mapper

import com.abhay.crypto.data.remote.dto.TickerPriceDto
import com.abhay.crypto.domain.model.Coin

object CoinMapper {

    fun toDomain(dto: TickerPriceDto): Coin = Coin(
        symbol = dto.symbol,
        baseAsset = dto.symbol.removeSuffix("USDT"),
        price = dto.price.toDoubleOrNull() ?: 0.0,
    )
}
