package com.raxim.myscoutee.profile.service;

import org.springframework.integration.mqtt.support.MqttHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessagingException;
import org.springframework.stereotype.Service;

import com.google.api.core.ApiFuture;
import com.google.api.core.ApiFutureCallback;
import com.google.api.core.ApiFutures;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Notification;
import com.raxim.myscoutee.common.config.MqttConfig.MqttGateway;
import com.raxim.myscoutee.profile.data.dto.rest.MessageDTO;
import com.raxim.myscoutee.profile.repository.mongo.TokenRepository;

@Service
public class MqttMessageService {
    private final MqttGateway mqttGateway;
    private final TokenRepository tokenRepository;

    public MqttMessageService(MqttGateway mqttGateway, TokenRepository tokenRepository) {
        this.mqttGateway = mqttGateway;
        this.tokenRepository = tokenRepository;
    }

    public void handleMessage(Message<?> message) throws MessagingException {
        String topic = (String) message.getHeaders().get(MqttHeaders.RECEIVED_TOPIC);
        System.out.println("Received message from topic: " + topic);

        MessageDTO messageDTO = (MessageDTO) message.getPayload();
        // the senderId should be added in the payload
        System.out.println("Received message: " + message.getPayload());

        String deviceKey = this.tokenRepository.findAll().get(0).getDeviceKey();

        com.google.firebase.messaging.Message fcmMessage = com.google.firebase.messaging.Message.builder()
                .setNotification(
                        Notification.builder()
                                .setTitle("Meet with Strangers - Notification has been activated!")
                                .build())
                .setToken(deviceKey)
                .build();

        try {
            ApiFuture<String> future = FirebaseMessaging.getInstance().sendAsync(fcmMessage);
            ApiFutures.addCallback(future, new ApiFutureCallback<String>() {
                @Override
                public void onSuccess(String result) {
                    System.out.println("Operation completed with result: " + result);
                }

                @Override
                public void onFailure(Throwable t) {
                    System.out.println("Operation failed with error: " + t);
                }
            }, MoreExecutors.directExecutor());

            FirebaseMessaging.getInstance().send(fcmMessage);
        } catch (FirebaseMessagingException e) {
            throw new MessagingException(deviceKey, e);
        }

        // it might need UUID to Base64 serialization
        mqttGateway.sendToMqtt("channels/profiles/" + messageDTO.getFrom(), "sent");
    }
}
