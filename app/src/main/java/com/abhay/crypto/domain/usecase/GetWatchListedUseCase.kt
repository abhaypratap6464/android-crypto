package com.abhay.crypto.domain.usecase

import com.abhay.crypto.domain.repository.WatchListRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetWatchListedUseCase @Inject constructor(
    private val repository: WatchListRepository,
) {
    operator fun invoke(): Flow<Set<String>> = repository.getWatchListed()
}
