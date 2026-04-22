package com.abhay.crypto.domain

import kotlinx.coroutines.flow.Flow

interface NetworkMonitor {
    val isAvailable: Flow<Boolean>
}
