package com.raxim.myscoutee.profile.data.dto.rest

import com.fasterxml.jackson.annotation.JsonProperty

data class Error (
    @field:JsonProperty(value = "key")
    val key: Int? = null,

    @field:JsonProperty(value = "value")
    val value: String? = null
)