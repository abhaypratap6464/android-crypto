package com.abhay.crypto.data.folder

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.abhay.crypto.domain.model.BookmarkFolder
import com.abhay.crypto.domain.repository.FolderRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FolderRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>,
    private val json: Json,
) : FolderRepository {

    private val key = stringPreferencesKey("folders")

    private fun decode(raw: String?): List<BookmarkFolder> =
        if (raw.isNullOrBlank()) emptyList()
        else runCatching { json.decodeFromString<List<BookmarkFolder>>(raw) }.getOrElse { emptyList() }

    override fun getFolders(): Flow<List<BookmarkFolder>> =
        dataStore.data.map { decode(it[key]) }

    override suspend fun createFolder(name: String, coinId: String?) {
        dataStore.edit { prefs ->
            val updated = decode(prefs[key]) + BookmarkFolder(
                id = UUID.randomUUID().toString(),
                name = name,
                coinIds = if (coinId != null) listOf(coinId) else emptyList(),
            )
            prefs[key] = json.encodeToString(updated)
        }
    }

    override suspend fun renameFolder(folderId: String, newName: String) {
        dataStore.edit { prefs ->
            val updated = decode(prefs[key]).map {
                if (it.id == folderId) it.copy(name = newName) else it
            }
            prefs[key] = json.encodeToString(updated)
        }
    }

    override suspend fun deleteFolder(folderId: String) {
        dataStore.edit { prefs ->
            val updated = decode(prefs[key]).filter { it.id != folderId }
            prefs[key] = json.encodeToString(updated)
        }
    }

    override suspend fun addCoinToFolder(folderId: String, coinId: String) {
        dataStore.edit { prefs ->
            val updated = decode(prefs[key]).map { folder ->
                if (folder.id == folderId && coinId !in folder.coinIds) {
                    folder.copy(coinIds = folder.coinIds + coinId)
                } else folder
            }
            prefs[key] = json.encodeToString(updated)
        }
    }

    override suspend fun removeCoinFromFolder(folderId: String, coinId: String) {
        dataStore.edit { prefs ->
            val updated = decode(prefs[key]).map { folder ->
                if (folder.id == folderId) folder.copy(coinIds = folder.coinIds - coinId)
                else folder
            }
            prefs[key] = json.encodeToString(updated)
        }
    }
}
