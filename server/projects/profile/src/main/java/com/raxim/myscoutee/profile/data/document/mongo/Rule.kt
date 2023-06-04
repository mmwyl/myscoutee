package com.raxim.myscoutee.profile.data.document.mongo

import com.fasterxml.jackson.annotation.JsonProperty
import com.querydsl.core.annotations.QueryEntity
import org.springframework.data.mongodb.core.mapping.Document

@QueryEntity
@Document(collection = "rules")
class Rule (
    @field:JsonProperty(value = "met")
    val met: Boolean? = null,

    // don't invite less than 2-3 rated people for the event
    @field:JsonProperty(value = "rate")
    val rate: Int? = null,

    @field:JsonProperty(value = "range")
    var range: RangeLocal? = RangeLocal(),

    // Balanced Female/Male Ratio
    @field:JsonProperty(value = "balanced")
    val balanced: String? = null
)