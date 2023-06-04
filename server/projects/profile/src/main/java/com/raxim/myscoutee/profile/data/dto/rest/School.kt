package com.raxim.myscoutee.profile.data.dto.rest

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty

data class School(

    @field:JsonProperty(value = "school")
    val school: com.raxim.myscoutee.profile.data.document.mongo.School? = null,

    @field:JsonProperty(value = "groupKey")
    val groupKey: Any? = null,

    @field:JsonIgnore
    val offset: List<Any>? = emptyList()
)