package com.raxim.myscoutee.profile.data.document.mongo

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import com.querydsl.core.annotations.QueryEntity
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.util.*

// index on uuid

@QueryEntity
@Document(collection = "tokens")
data class Token(
    @Id
    @field:JsonIgnore
    val id: UUID? = UUID.randomUUID(),

    // device registration key
    @get:JsonIgnore
    @set:JsonProperty(value = "deviceKey")
    var deviceKey: String? = null,

    //user uuid
    @field:JsonIgnore
    val uuid: UUID? = null
)