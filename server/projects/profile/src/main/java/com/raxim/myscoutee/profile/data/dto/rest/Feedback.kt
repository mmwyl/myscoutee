package com.raxim.myscoutee.profile.data.dto.rest

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty

data class Feedback(

    @field:JsonProperty(value = "feedback")
    val feedback: com.raxim.myscoutee.profile.data.document.mongo.Feedback? = null,

    @field:JsonIgnore
    val offset: List<Any>? = emptyList()
)