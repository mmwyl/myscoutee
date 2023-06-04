package com.raxim.myscoutee.profile.data.document.mongo

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import com.querydsl.core.annotations.QueryEntity
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.util.*

@QueryEntity
@Document(collection = "schools")
data class School(
    @Id
    @field:JsonProperty(value = "key")
    val id: UUID? = UUID.randomUUID(),

    @field:JsonProperty(value = "type")
    val type: String? = null,
    @field:JsonProperty(value = "name")
    val name: String? = null,
    // google
    @field:JsonProperty(value = "locationKey")
    val locationKey: String? = null,

    @field:JsonProperty(value = "range")
    val range: RangeLocal? = RangeLocal(),
    @field:JsonProperty(value = "role")
    val role: String? = null,

    @field:JsonIgnore
    var createdDate: Date? = Date(),

    // Active (A), Deleted (D)
    @field:JsonIgnore
    var status: String? = "A"
)