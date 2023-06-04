package com.raxim.myscoutee.profile.data.dto.rest

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty

data class Idea(

    @field:JsonProperty(value = "idea")
    val idea: com.raxim.myscoutee.profile.data.document.mongo.Idea? = null,

    @field:JsonIgnore
    val offset: List<Any>? = emptyList()
)