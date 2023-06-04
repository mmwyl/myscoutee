package com.raxim.myscoutee.profile.messaging

import org.springframework.messaging.support.MessageBuilder

fun <T> T.asMessage() =
    MessageBuilder
        .withPayload(this)
        .build()
