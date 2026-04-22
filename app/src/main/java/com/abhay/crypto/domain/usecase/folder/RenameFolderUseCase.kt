package com.abhay.crypto.domain.usecase.folder

import com.abhay.crypto.domain.repository.FolderRepository
import javax.inject.Inject

class RenameFolderUseCase @Inject constructor(
    private val repository: FolderRepository,
) {
    suspend operator fun invoke(folderId: String, newName: String) =
        repository.renameFolder(folderId, newName)
}
