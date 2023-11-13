package com.raxim.myscoutee.common.repository;

import java.io.IOException;
import java.util.UUID;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

public class UUIDBase64Serializer extends JsonSerializer<UUID> {

    @Override
    public void serialize(UUID uuid, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        // Serialize the UUID as a base64-encoded string
        String base64String = java.util.Base64.getEncoder().encodeToString(uuid.toString().getBytes());
        jsonGenerator.writeString(base64String);
    }
}
