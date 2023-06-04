package com.raxim.myscoutee.profile.data.dto.messaging

import com.fasterxml.jackson.annotation.JsonProperty

data class MessageContext(
    @get:JsonProperty
    val correlationId: String,
    @get:JsonProperty
    val requestId: String,
    @get:JsonProperty
    val sender: String,
    @get:JsonProperty
    val timeLeft: Int,
    @get:JsonProperty
    val userId: String
)
