package com.raxim.myscoutee.profile.data.document.mongo

import com.fasterxml.jackson.annotation.JsonProperty

data class RangeInt(
    @field:JsonProperty(value = "min")
    val min: Int = 0,

    @field:JsonProperty(value = "max")
    val max: Int = 0
)