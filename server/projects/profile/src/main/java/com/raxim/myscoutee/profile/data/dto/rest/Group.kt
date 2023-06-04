package com.raxim.myscoutee.profile.data.dto.rest

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty

data class Group(

    @field:JsonProperty(value = "group")
    val group: com.raxim.myscoutee.profile.data.document.mongo.Group? = null,

    @field:JsonProperty(value = "groupKey")
    val groupKey: Any? = null,

    @field:JsonProperty(value = "role")
    val role: Any? = null,

    @field:JsonIgnore
    val offset: List<Any>? = emptyList()
)