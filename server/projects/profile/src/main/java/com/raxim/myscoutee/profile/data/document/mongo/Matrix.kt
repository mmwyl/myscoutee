package com.raxim.myscoutee.profile.data.document.mongo

import com.fasterxml.jackson.annotation.JsonProperty

data class Matrix(
    @field:JsonProperty(value = "scale")
    val scale: Double? = 1.0,
    @field:JsonProperty(value = "angle")
    val angle: Double? = 0.0,
    @field:JsonProperty(value = "pos")
    val pos: Position? = Position()
)