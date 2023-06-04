package com.raxim.myscoutee.profile.data.document.mongo

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import com.querydsl.core.annotations.QueryEntity
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.util.*

@QueryEntity
@Document(collection = "messages")
class Message(
    @Id
    @JsonIgnore
    val id: UUID? = UUID.randomUUID(),

    @JsonIgnore
    val lang: String? = null,

    @field:JsonProperty(value = "msg")
    val msg: Map<String, Any>? = emptyMap()
)
