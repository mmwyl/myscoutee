package com.raxim.myscoutee.profile.service;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

public class JsonConverterService {

    private final ObjectMapper objectMapper;

    public JsonConverterService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public <T> T jsonToObject(String jsonString, Class<T> targetClass) {
        try {
            return objectMapper.readValue(jsonString, targetClass);
        } catch (IOException e) {
            // Handle the exception appropriately
            e.printStackTrace();
            return null;
        }
    }
}
