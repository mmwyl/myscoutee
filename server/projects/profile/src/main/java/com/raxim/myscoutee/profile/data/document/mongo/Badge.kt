package com.raxim.myscoutee.profile.data.document.mongo

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import com.querydsl.core.annotations.QueryEntity
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime
import java.util.*

@QueryEntity
@Document(collection = "badges")
data class Badge(
    @Id
    @field:JsonIgnore
    val id: UUID? = UUID.randomUUID(),

    @field:JsonProperty(value = "root")
    var root: String? = null,

    @field:JsonProperty(value = "value")
    val value: String? = null,

    //profile uuid
    @field:JsonIgnore
    val profileID: UUID? = null,

    //when value has been changed
    @field:JsonIgnore
    var dateUpdated: LocalDateTime? = null,

    //when route has been seen by the user, this date has been truncated to days (beginning of the day)
    @field:JsonIgnore
    var dateSeen: LocalDateTime? = null
)