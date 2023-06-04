package com.raxim.myscoutee.profile.data.document.mongo

import com.fasterxml.jackson.annotation.JsonProperty
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.util.*

@Document(collection = "amount")
data class Amount(
    //profile id - only for the query
    @Id
    @field:JsonProperty(value = "id")
    val id: UUID? = UUID.randomUUID(),
    @field:JsonProperty(value = "value")
    val value: Float? = null,
    @field:JsonProperty(value = "currency")
    val currency: String? = null

)