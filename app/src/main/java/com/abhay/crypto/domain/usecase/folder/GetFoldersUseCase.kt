package com.abhay.crypto.domain.usecase.folder

import com.abhay.crypto.domain.model.BookmarkFolder
import com.abhay.crypto.domain.repository.FolderRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetFoldersUseCase @Inject constructor(
    private val repository: FolderRepository,
) {
    operator fun invoke(): Flow<List<BookmarkFolder>> = repository.getFolders()
}
