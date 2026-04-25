package com.abhay.crypto.core.testing

import com.abhay.crypto.core.domain.repository.WatchListRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class FakeWatchListRepository : WatchListRepository {

    private val watchListedFlow = MutableStateFlow<Set<String>>(emptySet())

    override fun getWatchListed(): Flow<Set<String>> = watchListedFlow

    override suspend fun toggle(symbol: String) {
        val current = watchListedFlow.value
        if (current.contains(symbol)) {
            watchListedFlow.value = current - symbol
        } else {
            watchListedFlow.value = current + symbol
        }
    }

    fun setWatchListed(symbols: Set<String>) {
        watchListedFlow.value = symbols
    }
}
