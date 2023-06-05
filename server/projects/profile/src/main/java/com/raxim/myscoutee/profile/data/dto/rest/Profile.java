package com.raxim.myscoutee.profile.data.dto.rest;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.UUID;

public class Profile {
    @JsonProperty(value = "profile")
    private com.raxim.myscoutee.profile.data.document.mongo.Profile profile;

    @JsonProperty(value = "groupKey")
    private Object groupKey;

    @JsonProperty(value = "rate")
    private Integer rate;

    @JsonProperty(value = "ref")
    private UUID ref;

    @JsonIgnore
    private List<Object> offset;

    @JsonProperty(value = "role")
    private String role;

    public com.raxim.myscoutee.profile.data.document.mongo.Profile getProfile() {
        return profile;
    }

    public void setProfile(com.raxim.myscoutee.profile.data.document.mongo.Profile profile) {
        this.profile = profile;
    }

    public Object getGroupKey() {
        return groupKey;
    }

    public void setGroupKey(Object groupKey) {
        this.groupKey = groupKey;
    }

    public Integer getRate() {
        return rate;
    }

    public void setRate(Integer rate) {
        this.rate = rate;
    }

    public UUID getRef() {
        return ref;
    }

    public void setRef(UUID ref) {
        this.ref = ref;
    }

    public List<Object> getOffset() {
        return offset;
    }

    public void setOffset(List<Object> offset) {
        this.offset = offset;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
