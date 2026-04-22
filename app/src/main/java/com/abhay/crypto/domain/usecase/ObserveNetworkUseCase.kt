package com.abhay.crypto.domain.usecase

import com.abhay.crypto.domain.NetworkMonitor
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveNetworkUseCase @Inject constructor(
    private val networkMonitor: NetworkMonitor,
) {
    operator fun invoke(): Flow<Boolean> = networkMonitor.isAvailable
}
