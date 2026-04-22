package com.abhay.crypto.domain.usecase.folder

import com.abhay.crypto.domain.repository.FolderRepository
import javax.inject.Inject

class AddBookmarkToFolderUseCase @Inject constructor(
    private val repository: FolderRepository,
) {
    suspend operator fun invoke(folderId: String, coinId: String) =
        repository.addCoinToFolder(folderId, coinId)
}
