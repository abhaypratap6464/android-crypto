package com.abhay.crypto.core.domain.usecase

import com.abhay.crypto.core.domain.repository.WatchListRepository
import javax.inject.Inject

class ToggleWatchListUseCase @Inject constructor(
    private val repository: WatchListRepository,
) {
    suspend operator fun invoke(symbol: String) = repository.toggle(symbol)
}
