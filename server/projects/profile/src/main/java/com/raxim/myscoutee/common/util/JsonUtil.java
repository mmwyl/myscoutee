package com.raxim.myscoutee.common.util;

import java.io.IOException;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonUtil {
    public static final String ISO_DATE_STRING = "yyyy-MM-dd'T'HH:mm:ss.SSS";

    public static <T> T jsonToObject(String jsonString, Class<T> targetClass, ObjectMapper objectMapper) {
        try {
            return objectMapper.readValue(jsonString, targetClass);
        } catch (IOException e) {
            // Handle the exception appropriately
            e.printStackTrace();
            return null;
        }
    }

    public static <T> T clone(T t, ObjectMapper objectMapper) {
        try {
            String jsonString = objectMapper.writeValueAsString(t);
            return objectMapper.readValue(jsonString, new TypeReference<T>() {
            });
        } catch (IOException e) {
            // Handle the exception appropriately
            e.printStackTrace();
            return null;
        }
    }

}
