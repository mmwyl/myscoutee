package com.raxim.myscoutee.profile.data.dto.messaging

import com.fasterxml.jackson.annotation.JsonProperty

data class Envelope<T>(
    @get:JsonProperty
    val msgCtx: MessageContext,
    @get:JsonProperty
    val payload: T
)
