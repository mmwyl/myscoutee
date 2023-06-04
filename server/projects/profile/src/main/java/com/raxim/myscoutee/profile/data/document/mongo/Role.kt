package com.raxim.myscoutee.profile.data.document.mongo

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.util.*

@Document(collection = "roles")
data class Role(
    @Id
    val id: UUID? = UUID.randomUUID(),

    val profileId: UUID,

    val role: String,

    //active, suspended
    val status: String = "A",

    // event -> member ROLE_USER, ROLE_ADMIN
    val refId: UUID? = null
)