package com.raxim.myscoutee.profile.data.document.mongo

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.util.*

@Document(collection = "links")
data class Link(
    @Id
    @JsonIgnore
    val id: UUID? = UUID.randomUUID(),

    @field:JsonProperty(value = "key")
    val key: UUID? = null,

    @JsonIgnore
    val refId: UUID? = null,

    @JsonIgnore
    val type: String? = null,

    @JsonIgnore
    val usedBys: MutableSet<String> = mutableSetOf(),

    @field:JsonIgnore
    var createdDate: Date? = Date(),

    //profileId
    @field:JsonIgnore
    val createdBy: UUID? = null
)