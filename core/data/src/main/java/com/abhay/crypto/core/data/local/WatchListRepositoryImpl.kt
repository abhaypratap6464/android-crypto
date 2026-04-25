package com.abhay.crypto.core.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import com.abhay.crypto.core.domain.repository.WatchListRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WatchListRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>,
) : WatchListRepository {

    private val key = stringSetPreferencesKey("watch_listed")

    override fun getWatchListed(): Flow<Set<String>> =
        dataStore.data.map { it[key] ?: emptySet() }

    override suspend fun toggle(symbol: String) {
        dataStore.edit { prefs ->
            val current = prefs[key] ?: emptySet()
            prefs[key] = if (symbol in current) current - symbol else current + symbol
        }
    }
}
