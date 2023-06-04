package com.raxim.myscoutee.profile.data.document.mongo

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.util.*

// saving shortest path result from neo4j
@Document(collection = "paths")
data class Path(
    @Id
    @field:JsonProperty(value = "id")
    val id: UUID? = UUID.randomUUID(),

    @field:JsonProperty(value = "from")
    var from: UUID? = null,

    @field:JsonProperty(value = "to")
    var to: UUID? = null,

    // ref - like happened on idea, job, or promotion event
    @field:JsonIgnore
    var ref: UUID? = null,

    @field:JsonProperty(value = "distance")
    var distance: Long?
)