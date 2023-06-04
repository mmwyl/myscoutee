package com.raxim.myscoutee.profile.data.document.mongo

import com.fasterxml.jackson.annotation.JsonProperty

data class Image (
    @field:JsonProperty(value = "name")
    val name: String? = null,

    @field:JsonProperty(value = "mtx")
    val matrix: Matrix? = Matrix()
)