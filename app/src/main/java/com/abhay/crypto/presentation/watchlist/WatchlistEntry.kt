package com.abhay.crypto.presentation.watchlist

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.abhay.crypto.presentation.navigation.WatchlistNavKey

fun EntryProviderScope<NavKey>.watchlistEntry() {
    entry<WatchlistNavKey> {
        WatchlistScreen()
    }
}
