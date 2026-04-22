package com.abhay.crypto.di

import com.abhay.crypto.data.folder.FolderRepositoryImpl
import com.abhay.crypto.data.local.WatchListRepositoryImpl
import com.abhay.crypto.data.network.NetworkMonitorImpl
import com.abhay.crypto.data.repository.CoinRepositoryImpl
import com.abhay.crypto.domain.NetworkMonitor
import com.abhay.crypto.domain.repository.CoinRepository
import com.abhay.crypto.domain.repository.FolderRepository
import com.abhay.crypto.domain.repository.WatchListRepository
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
