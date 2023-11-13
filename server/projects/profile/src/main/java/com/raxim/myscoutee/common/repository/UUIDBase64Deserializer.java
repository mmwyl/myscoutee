package com.raxim.myscoutee.common.repository;

import java.util.UUID;
import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

public class UUIDBase64Deserializer extends JsonDeserializer<UUID> {

    @Override
    public UUID deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        String base64String = jsonParser.getValueAsString();
        byte[] uuidBytes = java.util.Base64.getDecoder().decode(base64String);
        return UUID.nameUUIDFromBytes(uuidBytes);
    }
}
