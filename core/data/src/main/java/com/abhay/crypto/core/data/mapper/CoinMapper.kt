package com.abhay.crypto.core.data.mapper

import com.abhay.crypto.core.domain.model.Coin
import com.abhay.crypto.core.network.dto.TickerPriceDto

object CoinMapper {

    fun toDomain(dto: TickerPriceDto): Coin = Coin(
        symbol = dto.symbol,
        baseAsset = dto.symbol.removeSuffix("USDT"),
        price = dto.price.toDoubleOrNull() ?: 0.0,
    )
}
