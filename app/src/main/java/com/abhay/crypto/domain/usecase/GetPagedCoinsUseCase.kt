package com.abhay.crypto.domain.usecase

import androidx.paging.PagingData
import com.abhay.crypto.domain.model.Coin
import com.abhay.crypto.domain.repository.CoinRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetPagedCoinsUseCase @Inject constructor(
    private val repository: CoinRepository,
) {
    operator fun invoke(): Flow<PagingData<Coin>> = repository.getPagedCoins()
}
