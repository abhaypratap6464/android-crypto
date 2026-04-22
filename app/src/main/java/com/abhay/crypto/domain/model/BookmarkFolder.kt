package com.abhay.crypto.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class BookmarkFolder(
    val id: String,
    val name: String,
    val coinIds: List<String>,
)
