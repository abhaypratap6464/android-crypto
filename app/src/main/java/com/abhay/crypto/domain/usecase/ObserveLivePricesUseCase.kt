package com.abhay.crypto.domain.usecase

import com.abhay.crypto.domain.repository.CoinRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveLivePricesUseCase @Inject constructor(
    private val repository: CoinRepository,
) {
    operator fun invoke(): Flow<Map<String, Double>> = repository.observeLivePrices()
}
