package com.raxim.myscoutee.profile.data.dto.rest

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import java.util.*

data class Reward(

    @field:JsonProperty(value = "name")
    val name: String? = null,

    @field:JsonProperty(value = "type")
    val type: String? = null,

    @field:JsonProperty(value = "cnt")
    val cnt: Int? = null,

    @field:JsonIgnore
    val ref: UUID? = null
)