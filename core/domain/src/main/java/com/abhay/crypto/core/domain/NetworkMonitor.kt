package com.abhay.crypto.core.domain

import kotlinx.coroutines.flow.Flow

interface NetworkMonitor {
    val isAvailable: Flow<Boolean>
}
