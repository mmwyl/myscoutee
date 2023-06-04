package com.raxim.myscoutee.profile.data.dto.rest

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty

data class Page<T>(
    @field:JsonProperty(value = "values")
    val values: List<T>? = emptyList(),

    @field:JsonProperty(value = "offset")
    val offset: List<Any>? = emptyList(),

    //scroll down only = 1
    @field:JsonProperty(value = "scroll")
    val scroll: Int? = 1,

    @field:JsonProperty(value = "step")
    val step: Any? = null,

    @field:JsonProperty(value = "role")
    val role: Any? = null

)