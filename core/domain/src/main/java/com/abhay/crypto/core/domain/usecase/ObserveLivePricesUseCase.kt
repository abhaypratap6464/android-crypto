package com.abhay.crypto.core.domain.usecase

import com.abhay.crypto.core.domain.repository.CoinRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveLivePricesUseCase @Inject constructor(
    private val repository: CoinRepository,
) {
    operator fun invoke(symbols: Flow<Set<String>>): Flow<Map<String, Double>> =
        repository.observeLivePrices(symbols)
}
