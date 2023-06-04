package com.raxim.myscoutee.profile.data.dto.rest

import com.fasterxml.jackson.annotation.JsonProperty

data class Code(

    @field:JsonProperty(value = "code")
    val code: String? = null
)