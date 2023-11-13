package com.raxim.myscoutee.profile.handler;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.MessagingException;

import com.raxim.myscoutee.profile.service.MqttMessageService;

public class MqttMessageHandler implements MessageHandler {

    private final MqttMessageService mqttMessageService;

    public MqttMessageHandler(MqttMessageService mqttMessageService) {
        this.mqttMessageService = mqttMessageService;
    }

    @Override
    public void handleMessage(Message<?> message) throws MessagingException {
        this.mqttMessageService.handleMessage(message);
    }
}
