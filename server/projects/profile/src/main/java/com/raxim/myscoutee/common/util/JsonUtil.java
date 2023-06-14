package com.raxim.myscoutee.common.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.UUIDDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;

public class JsonUtil {
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

    public static <T> T loadJson(Object obj, String jsonFile, Class<T> targetClass) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        
        SimpleModule module = new SimpleModule();
        module.addDeserializer(UUID.class, new UUIDDeserializer());
        objectMapper.registerModule(module);
        
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        Path filePath = FileUtil.getResourcePath(obj, jsonFile);
        String jsonData = new String(Files.readAllBytes(filePath));

        return objectMapper.readValue(jsonData, targetClass);
    }
}
