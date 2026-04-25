package com.abhay.crypto.core.data

import com.abhay.crypto.core.data.folder.FolderRepositoryImpl
import com.abhay.crypto.core.data.local.WatchListRepositoryImpl
import com.abhay.crypto.core.data.repository.CoinRepositoryImpl
import com.abhay.crypto.core.domain.NetworkMonitor
import com.abhay.crypto.core.domain.repository.CoinRepository
import com.abhay.crypto.core.domain.repository.FolderRepository
import com.abhay.crypto.core.domain.repository.WatchListRepository
import com.abhay.crypto.core.network.NetworkMonitorImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindCoinRepository(impl: CoinRepositoryImpl): CoinRepository

    @Binds
    @Singleton
    abstract fun bindFolderRepository(impl: FolderRepositoryImpl): FolderRepository

    @Binds
    @Singleton
    abstract fun bindNetworkMonitor(impl: NetworkMonitorImpl): NetworkMonitor

    @Binds
    @Singleton
    abstract fun bindWatchListRepository(impl: WatchListRepositoryImpl): WatchListRepository
}
