package com.raxim.myscoutee.profile.handler;

import org.springframework.integration.mqtt.support.MqttHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.MessagingException;

public class MqttMessageHandler implements MessageHandler {

    @Override
    public void handleMessage(Message<?> message) throws MessagingException {
        //the senderId should be added in the payload
        Integer cliendId = (Integer) message.getHeaders().get(MqttHeaders.ID);
        System.out.println("Received message from mqtt_id: " + cliendId);
        
        String topic = (String) message.getHeaders().get(MqttHeaders.RECEIVED_TOPIC);
        System.out.println("Received message from topic: " + topic);

        System.out.println("Received message: " + message.getPayload());
    }
}
