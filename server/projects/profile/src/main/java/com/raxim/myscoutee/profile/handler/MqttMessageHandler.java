package com.raxim.myscoutee.profile.handler;

import org.springframework.integration.mqtt.support.MqttHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.MessagingException;

public class MqttMessageHandler implements MessageHandler {

    @Override
    public void handleMessage(Message<?> message) throws MessagingException {
        //sender id should be in the payload of the message (maybe it's the mqtt_id)
        Integer cliendId = (Integer) message.getHeaders().get(MqttHeaders.ID);
        System.out.println("Received message from clientId: " + cliendId);
        
        String topic = (String) message.getHeaders().get(MqttHeaders.RECEIVED_TOPIC);
        System.out.println("Received message from topic: " + topic);

        System.out.println("Received message: " + message.getPayload());
    }
}
