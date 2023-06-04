package com.raxim.myscoutee.profile.data.dto.messaging

import com.fasterxml.jackson.annotation.JsonProperty
import org.springframework.format.annotation.DateTimeFormat
import java.time.LocalDateTime

data class Failure(
        @get:JsonProperty
        val description: String,
        @get:JsonProperty
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        val timestamp: LocalDateTime = LocalDateTime.now()
)
