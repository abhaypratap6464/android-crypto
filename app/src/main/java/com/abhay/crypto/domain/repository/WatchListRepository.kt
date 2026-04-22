package com.abhay.crypto.domain.repository

import kotlinx.coroutines.flow.Flow

interface WatchListRepository {
    fun getWatchListed(): Flow<Set<String>>
    suspend fun toggle(symbol: String)
}
