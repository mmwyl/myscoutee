package com.raxim.myscoutee.profile.data.dto.rest

import com.fasterxml.jackson.annotation.JsonProperty

class Like(
    @field:JsonProperty(value = "from")
    val from: String? = null,
    @field:JsonProperty(value = "to")
    val to: String? = null,
    @field:JsonProperty(value = "rate")
    val rate: Int? = null,
    @field:JsonProperty(value = "ref")
    val ref: String? = null
    )