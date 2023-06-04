package com.raxim.myscoutee.profile.data.dto.rest

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import java.util.*

data class Profile(

    @field:JsonProperty(value = "profile")
    val profile: com.raxim.myscoutee.profile.data.document.mongo.Profile? = null,

    @field:JsonProperty(value = "groupKey")
    val groupKey: Any? = null,

    @field:JsonProperty(value = "rate")
    val rate: Int? = null,

    @field:JsonProperty(value = "ref")
    val ref: UUID? = null,

    @field:JsonIgnore
    val offset: List<Any>? = emptyList(),

    @field:JsonProperty(value = "role")
    val role: String? = null

)