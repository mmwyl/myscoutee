package com.raxim.myscoutee.profile.data.document.mongo

import com.fasterxml.jackson.annotation.JsonProperty
import org.springframework.format.annotation.DateTimeFormat
import java.time.LocalDateTime

data class RangeLocal(
    @field:JsonProperty(value = "start")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    val start: LocalDateTime? = null,
    @field:JsonProperty(value = "end")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    val end: LocalDateTime? = null
)