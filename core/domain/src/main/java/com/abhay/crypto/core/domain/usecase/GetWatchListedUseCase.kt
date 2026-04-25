package com.abhay.crypto.core.domain.usecase

import com.abhay.crypto.core.domain.repository.WatchListRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetWatchListedUseCase @Inject constructor(
    private val repository: WatchListRepository,
) {
    operator fun invoke(): Flow<Set<String>> = repository.getWatchListed()
}
