package com.raxim.myscoutee.profile.converter;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.integration.mqtt.support.DefaultPahoMessageConverter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.raxim.myscoutee.common.util.JsonUtil;
import com.raxim.myscoutee.profile.data.dto.rest.MessageDTO;

public class MqttMessageConverter extends DefaultPahoMessageConverter {

    private final ObjectMapper objectMapper;

    public MqttMessageConverter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    protected Object mqttBytesToPayload(MqttMessage mqttMessage) {
        try {
            String payloadStr = new String(mqttMessage.getPayload(), StandardCharsets.UTF_8.name());
            return JsonUtil.jsonToObject(payloadStr, MessageDTO.class, objectMapper);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    /*
     * private MessageDTO convertPayload(Object payload) {
     * // Implement your logic to convert the payload to MessageDTO
     * // This might involve type casting or mapping depending on your application's
     * requirements
     * // Replace the logic below with your actual conversion logic
     * if (payload instanceof MessageDTO) {
     * return (MessageDTO) payload;
     * } else {
     * // Handle other cases or throw an exception if the conversion is not possible
     * throw new
     * IllegalArgumentException("Payload cannot be converted to MessageDTO");
     * }
     * }
     */
}