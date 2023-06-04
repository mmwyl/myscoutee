package com.raxim.myscoutee.profile.data.document.mongo

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import com.querydsl.core.annotations.QueryEntity
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.util.*

@QueryEntity
@Document(collection = "feedbacks")
data class Feedback(
    @Id
    @field:JsonProperty(value = "key")
    val id: UUID? = UUID.randomUUID(),
    // panel background coloring depends on rate
    @field:JsonProperty(value = "rate")
    val rate: Int? = null,
    @field:JsonProperty(value = "desc")
    val desc: String? = null,

    @field:JsonIgnore
    var createdDate: Date? = Date()
)