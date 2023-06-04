package com.raxim.myscoutee.profile.data.document.mongo

import org.springframework.data.mongodb.core.mapping.DBRef
import org.springframework.data.mongodb.core.mapping.Document

@Document
data class Neo4jLike(
    @DBRef
    val from: Profile? = null,
    @DBRef
    val to: Profile? = null,

    val distance: Long? = null,
    val rate: Double? = null
)
