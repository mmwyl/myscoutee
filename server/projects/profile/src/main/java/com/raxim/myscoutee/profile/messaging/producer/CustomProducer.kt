package com.raxim.myscoutee.profile.messaging.producer

import com.raxim.myscoutee.profile.data.dto.messaging.Envelope
import com.raxim.myscoutee.profile.data.dto.messaging.output.OutData
import com.raxim.myscoutee.profile.message.Notification
import com.raxim.myscoutee.profile.messaging.asMessage
import org.springframework.cloud.stream.annotation.EnableBinding
import org.springframework.cloud.stream.annotation.Output
import org.springframework.messaging.MessageChannel
import org.springframework.messaging.handler.annotation.SendTo
import org.springframework.stereotype.Component

interface CustomSource {
    companion object {
        const val CUSTOM_OUT = "profilemanagement-out"
        const val KAFKA_OUT = "externalNotif"
    }

    @Output(CUSTOM_OUT)
    fun customOut(): MessageChannel

    @Output(KAFKA_OUT)
    fun kafkaOut(): MessageChannel
}

//@Component
//@EnableBinding(CustomSource::class)
class CustomProducer(
    private val source: CustomSource
) {
    @SendTo(CustomSource.CUSTOM_OUT)
    fun sendToCustomOut(payload: Envelope<OutData>) {
        source.customOut().send(payload.asMessage())
    }

    @SendTo(CustomSource.KAFKA_OUT)
    fun sendToKafkaOut(payload: Notification) {
        source.kafkaOut().send(payload.asMessage())
    }
}