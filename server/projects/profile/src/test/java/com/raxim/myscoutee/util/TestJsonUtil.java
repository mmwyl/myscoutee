package com.raxim.myscoutee.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.raxim.myscoutee.common.util.FileUtil;

public class TestJsonUtil {

    public static <T> T loadJson(Object obj, String jsonFile, Class<T> targetClass, ObjectMapper objectMapper)
            throws IOException {
        Path filePath = FileUtil.getResourcePath(obj, jsonFile);
        String jsonData = new String(Files.readAllBytes(filePath));

        return objectMapper.readValue(jsonData, targetClass);
    }

    public static String jsonToString(Object object, ObjectMapper objectMapper) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (IOException e) {
            // Handle the exception appropriately
            e.printStackTrace();
            return null;
        }
    }
}
