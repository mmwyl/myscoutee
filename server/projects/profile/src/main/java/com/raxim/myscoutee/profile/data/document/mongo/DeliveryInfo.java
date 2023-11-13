package com.raxim.myscoutee.profile.data.document.mongo;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DeliveryInfo {
    @JsonProperty(value = "to")
    private UUID to;

    //firebase or mqtt
    @JsonProperty(value = "type")
    private String type;

    public UUID getTo() {
        return to;
    }

    public void setTo(UUID to) {
        this.to = to;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
