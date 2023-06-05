package com.raxim.myscoutee.profile.data.dto.rest;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class Member {
    @JsonProperty(value = "member")
    private com.raxim.myscoutee.profile.data.document.mongo.Member member;

    @JsonIgnore
    private List<Object> offset;

    @JsonProperty(value = "role")
    private String role;

    public com.raxim.myscoutee.profile.data.document.mongo.Member getMember() {
        return member;
    }

    public void setMember(com.raxim.myscoutee.profile.data.document.mongo.Member member) {
        this.member = member;
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
