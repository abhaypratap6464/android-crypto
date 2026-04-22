package com.abhay.crypto.domain.usecase.folder

import com.abhay.crypto.domain.repository.FolderRepository
import javax.inject.Inject

class CreateFolderUseCase @Inject constructor(
    private val repository: FolderRepository,
) {
    suspend operator fun invoke(name: String, coinId: String? = null) =
        repository.createFolder(name, coinId)
}
