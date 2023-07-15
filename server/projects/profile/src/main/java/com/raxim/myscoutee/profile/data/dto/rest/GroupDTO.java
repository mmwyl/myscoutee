package com.raxim.myscoutee.profile.data.dto.rest;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.raxim.myscoutee.profile.data.document.mongo.Group;

@JsonRootName("group")
public class GroupDTO extends PageItemDTO {

    @JsonProperty(value = "group")
    private Group group;

    @JsonProperty(value = "groupKey")
    private Object groupKey;

    // friends only vs all
    @JsonProperty(value = "access")
    private String access;

    @JsonProperty(value = "role")
    private Object role;

    @JsonIgnore
    private List<Object> offset;

    public GroupDTO() {
    }

    public GroupDTO(Group group) {
        this.group = group;
    }

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    public Object getGroupKey() {
        return groupKey;
    }

    public void setGroupKey(Object groupKey) {
        this.groupKey = groupKey;
    }

    public Object getRole() {
        return role;
    }

    public void setRole(Object role) {
        this.role = role;
    }

    public List<Object> getOffset() {
        return offset;
    }

    public void setOffset(List<Object> offset) {
        this.offset = offset;
    }

    public String getAccess() {
        return access;
    }

    public void setAccess(String access) {
        this.access = access;
    }
}
