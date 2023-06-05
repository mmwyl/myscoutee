package com.raxim.myscoutee.profile.data.dto.rest;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class Promotion {
    @JsonProperty(value = "promotion")
    private com.raxim.myscoutee.profile.data.document.mongo.Promotion promotion;

    @JsonIgnore
    private List<Object> offset;

    public com.raxim.myscoutee.profile.data.document.mongo.Promotion getPromotion() {
        return promotion;
    }

    public void setPromotion(com.raxim.myscoutee.profile.data.document.mongo.Promotion promotion) {
        this.promotion = promotion;
    }

    public List<Object> getOffset() {
        return offset;
    }

    public void setOffset(List<Object> offset) {
        this.offset = offset;
    }
}
