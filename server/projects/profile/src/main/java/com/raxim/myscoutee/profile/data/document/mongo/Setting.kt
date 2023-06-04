package com.raxim.myscoutee.profile.data.document.mongo

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.util.*

@Document(collection = "settings")
data class Setting(
    //profile id - only for the query
    @Id
    @field:JsonProperty(value = "id")
    val id: UUID? = UUID.randomUUID(),

    // search by page identifier
    @field:JsonProperty(value = "key")
    var key: String? = null,

    // settings by page and profile
    @field:JsonIgnore
    val profile: UUID? = null,

    @field:JsonProperty(value = "items")
    val items: List<FormItem>? = emptyList()
)