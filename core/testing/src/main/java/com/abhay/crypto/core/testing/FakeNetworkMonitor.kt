package com.abhay.crypto.core.testing

import com.abhay.crypto.core.domain.NetworkMonitor
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class FakeNetworkMonitor : NetworkMonitor {

    private val isAvailableFlow = MutableStateFlow(true)

    override val isAvailable: Flow<Boolean> = isAvailableFlow

    fun setAvailable(available: Boolean) {
        isAvailableFlow.value = available
    }
}
