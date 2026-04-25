package com.abhay.crypto

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberNavBackStack
import com.abhay.crypto.feature.watchlist.api.WatchlistNavKey

@Composable
fun rememberCryptoAppState(
    startDestination: NavKey = WatchlistNavKey
): CryptoAppState {
    val backStack = rememberNavBackStack(startDestination)
    return remember(backStack) {
        CryptoAppState(backStack)
    }
}

@Stable
class CryptoAppState(
    val backStack: MutableList<NavKey>
) {
    val currentDestination: NavKey?
        get() = backStack.lastOrNull()

    fun navigateTo(destination: NavKey) {
        backStack.add(destination)
    }

    fun goBack() {
        backStack.removeLastOrNull()
    }
}
