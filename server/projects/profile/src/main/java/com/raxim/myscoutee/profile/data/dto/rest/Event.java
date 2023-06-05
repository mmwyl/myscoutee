package com.raxim.myscoutee.profile.data.dto.rest;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class Event {
    @JsonProperty(value = "event")
    private com.raxim.myscoutee.profile.data.document.mongo.Event event;

    @JsonProperty(value = "groupKey")
    private Object groupKey;

    @JsonProperty(value = "sortKey")
    private Object sortKey;

    @JsonIgnore
    private List<Object> offset;

    @JsonProperty(value = "rate")
    private Integer rate;

    @JsonProperty(value = "groupType")
    private String groupType;

    @JsonProperty(value = "promoType")
    private String promoType;

    @JsonProperty(value = "isPromotion")
    private Boolean isPromotion;

    @JsonProperty(value = "role")
    private String role;

    public com.raxim.myscoutee.profile.data.document.mongo.Event getEvent() {
        return event;
    }

    public void setEvent(com.raxim.myscoutee.profile.data.document.mongo.Event event) {
        this.event = event;
    }

    public Object getGroupKey() {
        return groupKey;
    }

    public void setGroupKey(Object groupKey) {
        this.groupKey = groupKey;
    }

    public Object getSortKey() {
        return sortKey;
    }

    public void setSortKey(Object sortKey) {
        this.sortKey = sortKey;
    }

    public List<Object> getOffset() {
        return offset;
    }

    public void setOffset(List<Object> offset) {
        this.offset = offset;
    }

    public Integer getRate() {
        return rate;
    }

    public void setRate(Integer rate) {
        this.rate = rate;
    }

    public String getGroupType() {
        return groupType;
    }

    public void setGroupType(String groupType) {
        this.groupType = groupType;
    }

    public String getPromoType() {
        return promoType;
    }

    public void setPromoType(String promoType) {
        this.promoType = promoType;
    }

    public Boolean getIsPromotion() {
        return isPromotion;
    }

    public void setIsPromotion(Boolean isPromotion) {
        this.isPromotion = isPromotion;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
