package com.raxim.myscoutee.profile.data.dto.rest;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.raxim.myscoutee.profile.data.document.mongo.DBMessage;

@JsonRootName("message")
public class MessageDTO extends PageItemDTO {

    @JsonProperty(value = "message")
    private DBMessage message;

    @JsonProperty(value = "from")
    private ImageDTO from;

    @JsonProperty(value = "name")
    private String name;

    // profile icon image urls, who read the message
    @JsonProperty(value = "reads")
    private List<ImageDTO> reads;

    public DBMessage getMessage() {
        return message;
    }

    public void setMessage(DBMessage message) {
        this.message = message;
    }

    public ImageDTO getFrom() {
        return from;
    }

    public void setFrom(ImageDTO from) {
        this.from = from;
    }

    public List<ImageDTO> getReads() {
        return reads;
    }

    public void setReads(List<ImageDTO> reads) {
        this.reads = reads;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
