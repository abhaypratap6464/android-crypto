package com.abhay.crypto.feature.watchlist.impl

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import com.abhay.crypto.core.domain.model.Coin
import kotlinx.coroutines.flow.flowOf
import org.junit.Rule
import org.junit.Test

class WatchlistScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun watchlistScreen_showsTitle() {
        val uiState = WatchlistUiState(
            isNetworkAvailable = true,
            folders = emptyList(),
        )

        composeTestRule.setContent {
            val lazyPagingItems = flowOf(
                PagingData.from(
                    listOf(
                        Coin("BTCUSDT", "BTC", 50000.0),
                        Coin("ETHUSDT", "ETH", 3000.0)
                    )
                )
            ).collectAsLazyPagingItems()

            WatchlistContent(
                lazyPagingItems = lazyPagingItems,
                uiState = uiState,
                onEvent = {},
                priceProvider = { null },
                formatPrice = { "$it" }
            )
        }

        composeTestRule.onNodeWithText("Watchlist").assertIsDisplayed()
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun watchlistScreen_showsNetworkBanner_whenOffline() {
        val uiState = WatchlistUiState(
            isNetworkAvailable = false,
            folders = emptyList(),
        )

        composeTestRule.setContent {
            val lazyPagingItems = flowOf(PagingData.empty<Coin>()).collectAsLazyPagingItems()

            WatchlistContent(
                lazyPagingItems = lazyPagingItems,
                uiState = uiState,
                onEvent = {},
                priceProvider = { null },
                formatPrice = { "$it" }
            )
        }

        // Check for NetworkBanner content (No internet status message from strings.xml)
        composeTestRule.waitUntilAtLeastOneExists(hasText("No internet", substring = true), 5000)
        composeTestRule.onNodeWithText("No internet", substring = true).assertIsDisplayed()
    }
}
