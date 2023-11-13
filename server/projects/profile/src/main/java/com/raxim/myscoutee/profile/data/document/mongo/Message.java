package com.raxim.myscoutee.profile.data.document.mongo;

import java.util.List;
import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.threeten.bp.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

@Document(collection = "messages")
public class Message {
    @Id
    @JsonProperty(value = "key")
    private UUID id;

    @JsonProperty(value = "from")
    private UUID from;

    @JsonProperty(value = "tos")
    private List<DeliveryInfo> tos;

    // control, mqtt=if profile.mqtt is true, and the topic is the current page url
    // of the profile then does not send as mosquitto handles
    @JsonProperty(value = "type")
    private String type;

    // writing, received, sent (last received date by user in the query for which
    // message should put the icon on the ui)
    @JsonProperty(value = "action")
    private String action;

    @JsonIgnore
    private LocalDateTime createdDate;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getFrom() {
        return from;
    }

    public void setFrom(UUID from) {
        this.from = from;
    }

    public List<DeliveryInfo> getTos() {
        return tos;
    }

    public void setTos(List<DeliveryInfo> tos) {
        this.tos = tos;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }
}
