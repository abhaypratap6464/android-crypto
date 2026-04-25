package com.abhay.crypto.core.domain.repository

import com.abhay.crypto.core.domain.model.BookmarkFolder
import kotlinx.coroutines.flow.Flow

interface FolderRepository {
    fun getFolders(): Flow<List<BookmarkFolder>>
    suspend fun createFolder(name: String, coinId: String? = null)
    suspend fun renameFolder(folderId: String, newName: String)
    suspend fun deleteFolder(folderId: String)
    suspend fun addCoinToFolder(folderId: String, coinId: String)
    suspend fun removeCoinFromFolder(folderId: String, coinId: String)
}
