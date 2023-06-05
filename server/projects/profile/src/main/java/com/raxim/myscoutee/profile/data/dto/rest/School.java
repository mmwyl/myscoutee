package com.raxim.myscoutee.profile.data.dto.rest;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class School {
    @JsonProperty(value = "school")
    private com.raxim.myscoutee.profile.data.document.mongo.School school;

    @JsonProperty(value = "groupKey")
    private Object groupKey;

    @JsonIgnore
    private List<Object> offset;

    public com.raxim.myscoutee.profile.data.document.mongo.School getSchool() {
        return school;
    }

    public void setSchool(com.raxim.myscoutee.profile.data.document.mongo.School school) {
        this.school = school;
    }

    public Object getGroupKey() {
        return groupKey;
    }

    public void setGroupKey(Object groupKey) {
        this.groupKey = groupKey;
    }

    public List<Object> getOffset() {
        return offset;
    }

    public void setOffset(List<Object> offset) {
        this.offset = offset;
    }
}
