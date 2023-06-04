package com.raxim.myscoutee.profile.data.dto.rest

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty

data class Promotion(

    @field:JsonProperty(value = "promotion")
    val promotion: com.raxim.myscoutee.profile.data.document.mongo.Promotion? = null,

    @field:JsonIgnore
    val offset: List<Any>? = emptyList()
)