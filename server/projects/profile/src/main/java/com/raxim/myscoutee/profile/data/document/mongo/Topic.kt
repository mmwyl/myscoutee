package com.raxim.myscoutee.profile.data.document.mongo

import org.springframework.data.mongodb.core.mapping.DBRef
import org.springframework.data.mongodb.core.mapping.Document

@Document
data class Topic(
    val name: String? = null,
    val tokens: List<String>? = emptyList()
)
