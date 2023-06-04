package com.raxim.myscoutee.profile.data.document.mongo

import com.fasterxml.jackson.annotation.JsonProperty
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.util.*

@Document(collection = "forms")
class Form(
    @Id
    @field:JsonProperty(value = "id")
    val id: UUID? = UUID.randomUUID(),

    @field:JsonProperty(value = "key")
    var key: String? = null,

    @field:JsonProperty(value = "items")
    val items: List<FormItem>? = emptyList()
)