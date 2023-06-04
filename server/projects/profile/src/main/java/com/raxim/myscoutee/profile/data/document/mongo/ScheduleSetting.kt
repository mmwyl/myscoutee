package com.raxim.myscoutee.profile.data.document.mongo

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import com.querydsl.core.annotations.QueryEntity
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.util.*

@QueryEntity
@Document(collection = "scheduleSettings")
class ScheduleSetting(
    @Id
    @field:JsonProperty(value = "id")
    val id: UUID? = UUID.randomUUID(),

    @field:JsonProperty(value = "key")
    var key: String? = null,

    @field:JsonProperty(value = "flags")
    var flags: String? = null
)