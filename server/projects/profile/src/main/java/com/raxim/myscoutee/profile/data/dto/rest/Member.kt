package com.raxim.myscoutee.profile.data.dto.rest

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty

data class Member(

    @field:JsonProperty(value = "member")
    val member: com.raxim.myscoutee.profile.data.document.mongo.Member? = null,

    @field:JsonIgnore
    val offset: List<Any>? = emptyList(),

    @field:JsonProperty(value = "role")
    var role: String? = null
)