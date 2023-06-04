package com.raxim.myscoutee.profile.data.document.mongo

import com.fasterxml.jackson.annotation.JsonIgnore
import com.querydsl.core.annotations.QueryEntity
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.DBRef
import org.springframework.data.mongodb.core.mapping.Document
import java.util.*

@QueryEntity
@Document(collection = "users")
data class User(
    @Id
    val id: UUID? = UUID.randomUUID(),

    // load by email
    @JsonIgnore
    val email: String,

    @JsonIgnore
    var createdDate: Date? = Date(),

    // active group
    var group: UUID? = null,

    // active profile
    @DBRef
    var profile: Profile? = null,

    @DBRef
    @JsonIgnore
    var profiles: MutableSet<Profile>? = mutableSetOf()
)