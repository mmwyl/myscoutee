package com.raxim.myscoutee.profile.messaging.consumer

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonTypeRef
import com.raxim.myscoutee.profile.data.dto.messaging.Envelope
import com.raxim.myscoutee.profile.data.dto.messaging.input.InData
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.springframework.cloud.stream.annotation.EnableBinding
import org.springframework.cloud.stream.annotation.Input
import org.springframework.cloud.stream.annotation.StreamListener
import org.springframework.messaging.SubscribableChannel
import org.springframework.stereotype.Component

interface CustomSink {
    companion object {
        const val CUSTOM_IN = "profilemanagement-in"
    }

    @Input(CUSTOM_IN)
    fun customIn(): SubscribableChannel
}

//@Component
//@EnableBinding(CustomSink::class)
class CustomConsumer(
        private val objectMapper: ObjectMapper
) {
    val logger: Log = LogFactory.getLog(javaClass)

    @StreamListener(target = CustomSink.CUSTOM_IN)
    fun handleCustomIn(data: ByteArray) {
        val env = objectMapper.readValue(data, jacksonTypeRef<Envelope<InData>>())
        logger.info(env)
    }
}
