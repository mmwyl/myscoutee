package com.raxim.myscoutee.profile.handler;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.MessagingException;

import com.raxim.myscoutee.profile.service.MessageService;

public class MqttMessageHandler implements MessageHandler {

    private final MessageService mqttMessageService;

    public MqttMessageHandler(MessageService mqttMessageService) {
        this.mqttMessageService = mqttMessageService;
    }

    @Override
    public void handleMessage(Message<?> message) throws MessagingException {
        this.mqttMessageService.handleMessage(message);
    }
}
