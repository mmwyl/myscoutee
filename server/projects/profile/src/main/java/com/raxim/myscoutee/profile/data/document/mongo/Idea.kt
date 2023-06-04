package com.raxim.myscoutee.profile.data.document.mongo

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import com.querydsl.core.annotations.QueryEntity
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.util.*

@QueryEntity
@Document(collection = "ideas")
data class Idea(
    @Id
    @field:JsonProperty(value = "key")
    val id: UUID? = UUID.randomUUID(),

    @field:JsonProperty(value = "name")
    val name: String? = null,

    @field:JsonProperty(value = "desc")
    val desc: String? = null,

    @field:JsonIgnore
    var createdDate: Date? = Date(),

    @field:JsonIgnore
    val createdBy: UUID? = null
)