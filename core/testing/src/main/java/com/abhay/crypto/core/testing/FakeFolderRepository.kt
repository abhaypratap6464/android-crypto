package com.abhay.crypto.core.testing

import com.abhay.crypto.core.domain.model.BookmarkFolder
import com.abhay.crypto.core.domain.repository.FolderRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import java.util.UUID

class FakeFolderRepository : FolderRepository {

    private val foldersFlow = MutableStateFlow<List<BookmarkFolder>>(emptyList())

    override fun getFolders(): Flow<List<BookmarkFolder>> = foldersFlow

    override suspend fun createFolder(name: String, coinId: String?) {
        val newFolder = BookmarkFolder(
            id = UUID.randomUUID().toString(),
            name = name,
            coinIds = coinId?.let { listOf(it) } ?: emptyList()
        )
        foldersFlow.value = foldersFlow.value + newFolder
    }

    override suspend fun renameFolder(folderId: String, newName: String) {
        foldersFlow.value = foldersFlow.value.map {
            if (it.id == folderId) it.copy(name = newName) else it
        }
    }

    override suspend fun deleteFolder(folderId: String) {
        foldersFlow.value = foldersFlow.value.filterNot { it.id == folderId }
    }

    override suspend fun addCoinToFolder(folderId: String, coinId: String) {
        foldersFlow.value = foldersFlow.value.map {
            if (it.id == folderId && !it.coinIds.contains(coinId)) {
                it.copy(coinIds = it.coinIds + coinId)
            } else it
        }
    }

    override suspend fun removeCoinFromFolder(folderId: String, coinId: String) {
        foldersFlow.value = foldersFlow.value.map {
            if (it.id == folderId) {
                it.copy(coinIds = it.coinIds - coinId)
            } else it
        }
    }

    fun setFolders(folders: List<BookmarkFolder>) {
        foldersFlow.value = folders
    }
}
