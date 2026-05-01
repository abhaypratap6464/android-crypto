package com.abhay.crypto.core.network.dto

import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.Serializable

@OptIn(InternalSerializationApi::class)
@Serializable
data class BinanceSubscriptionMessage(
    val method: String,
    val params: List<String>,
    val id: Int
) {
    companion object {
        const val SUBSCRIBE = "SUBSCRIBE"
        const val UNSUBSCRIBE = "UNSUBSCRIBE"
    }
}
