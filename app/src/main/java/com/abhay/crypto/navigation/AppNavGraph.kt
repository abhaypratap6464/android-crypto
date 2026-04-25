package com.abhay.crypto.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.abhay.crypto.CryptoAppState
import com.abhay.crypto.NavEntryBuilder
import com.abhay.crypto.rememberCryptoAppState

@Composable
fun NavGraph(
    appState: CryptoAppState = rememberCryptoAppState(),
    entryBuilders: Set<NavEntryBuilder>
) {
    NavDisplay(
        backStack = appState.backStack,
        onBack = appState::goBack,
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator(),
        ),
        entryProvider = entryProvider {
            entryBuilders.forEach { builder ->
                builder()
            }
        },
    )
}
