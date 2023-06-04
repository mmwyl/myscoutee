package com.raxim.myscoutee.profile.data.dto.rest

import com.fasterxml.jackson.annotation.JsonProperty

data class ProfileStatus(
    @field:JsonProperty(value = "status")
    val status: String? = null,

    @field:JsonProperty(value = "role")
    val role: String? = null
)