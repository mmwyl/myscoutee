package com.raxim.myscoutee.profile.data.dto.rest;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class Idea {
    @JsonProperty(value = "idea")
    private com.raxim.myscoutee.profile.data.document.mongo.Idea idea;

    @JsonIgnore
    private List<Object> offset;

    public com.raxim.myscoutee.profile.data.document.mongo.Idea getIdea() {
        return idea;
    }

    public void setIdea(com.raxim.myscoutee.profile.data.document.mongo.Idea idea) {
        this.idea = idea;
    }

    public List<Object> getOffset() {
        return offset;
    }

    public void setOffset(List<Object> offset) {
        this.offset = offset;
    }
}
