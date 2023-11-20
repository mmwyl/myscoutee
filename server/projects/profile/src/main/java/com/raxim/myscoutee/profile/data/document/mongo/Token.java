package com.raxim.myscoutee.profile.data.document.mongo;

import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

@Document(collection = "tokens")
public class Token {
    @Id
    @JsonIgnore
    private UUID id;

    @JsonProperty(value = "deviceKey")
    private String deviceKey;

    // mqtt, firebase
    @JsonIgnore
    private String type;

    //userUUId
    @JsonIgnore
    private UUID uuid;

    public UUID getUuid() {
        return uuid;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    @JsonIgnore
    public String getDeviceKey() {
        return deviceKey;
    }

    public void setDeviceKey(String deviceKey) {
        this.deviceKey = deviceKey;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public UUID getId() {
        return id;
    }
}
