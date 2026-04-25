package com.abhay.crypto.core.domain.usecase.folder

import com.abhay.crypto.core.domain.repository.FolderRepository
import javax.inject.Inject

class DeleteFolderUseCase @Inject constructor(
    private val repository: FolderRepository,
) {
    suspend operator fun invoke(folderId: String) =
        repository.deleteFolder(folderId)
}
