package com.raxim.myscoutee.profile.data.dto.rest

import com.fasterxml.jackson.annotation.JsonProperty

data class LinkInfo(
    @field:JsonProperty(value = "title")
    val title: String? = null,
    @field:JsonProperty(value = "msg")
    val msg: String? = null
)