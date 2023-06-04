package com.raxim.myscoutee.profile.data.dto.rest

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty

data class Job(

    @field:JsonProperty(value = "job")
    val job: com.raxim.myscoutee.profile.data.document.mongo.Job? = null,

    @field:JsonIgnore
    val offset: List<Any>? = emptyList()
)