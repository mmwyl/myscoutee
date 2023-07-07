package com.raxim.myscoutee.profile.data.dto.rest;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.raxim.myscoutee.profile.converter.Convertable;
import com.raxim.myscoutee.profile.data.document.mongo.Event;

@JsonRootName("event")
public class EventDTO extends PageItemDTO implements Convertable {

    @JsonProperty(value = "event")
    private Event event;

    @JsonProperty(value = "groupKey")
    private Object groupKey;

    @JsonProperty(value = "sortKey")
    private Object sortKey;

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

    public EventDTO(Event event) {
        this.event = event;
    }

    public EventDTO() {
    }

    public EventDTO(Event event, Object groupKey, Object sortKey) {
        this.event = event;
        this.groupKey = groupKey;
        this.sortKey = sortKey;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
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

    public Boolean getPromotion() {
        return isPromotion;
    }

    public void setPromotion(Boolean promotion) {
        isPromotion = promotion;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
