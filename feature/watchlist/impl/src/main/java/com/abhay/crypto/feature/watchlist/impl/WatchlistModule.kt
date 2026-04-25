package com.abhay.crypto.feature.watchlist.impl

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.abhay.crypto.feature.watchlist.api.WatchlistNavKey
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.multibindings.IntoSet

@Module
@InstallIn(ActivityRetainedComponent::class)
object WatchlistModule {

    @Provides
    @IntoSet
    fun provideWatchlistEntry(): @JvmSuppressWildcards (EntryProviderScope<NavKey>.() -> Unit) = {
        entry<WatchlistNavKey> {
            WatchlistScreen()
        }
    }
}
