package com.abhay.crypto.core.domain.usecase.folder

import com.abhay.crypto.core.domain.repository.FolderRepository
import javax.inject.Inject

class CreateFolderUseCase @Inject constructor(
    private val repository: FolderRepository,
) {
    suspend operator fun invoke(name: String, coinId: String? = null) =
        repository.createFolder(name, coinId)
}
