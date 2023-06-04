package com.raxim.myscoutee.profile.data.dto.rest

import com.fasterxml.jackson.annotation.JsonProperty
import com.raxim.myscoutee.profile.data.document.mongo.Badge

data class User(
    @field:JsonProperty(value = "user")
    val user: com.raxim.myscoutee.profile.data.document.mongo.User? = null,

    @field:JsonProperty(value = "groups")
    val groups: List<Group>? = null,

    @field:JsonProperty(value = "likes")
    val likes: List<Badge>? = null
)