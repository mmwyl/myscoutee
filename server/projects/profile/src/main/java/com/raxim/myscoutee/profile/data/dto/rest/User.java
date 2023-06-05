package com.raxim.myscoutee.profile.data.dto.rest;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.raxim.myscoutee.profile.data.document.mongo.Badge;
import java.util.List;

public class User {
    @JsonProperty(value = "user")
    private com.raxim.myscoutee.profile.data.document.mongo.User user;

    @JsonProperty(value = "groups")
    private List<Group> groups;

    @JsonProperty(value = "likes")
    private List<Badge> likes;

    public com.raxim.myscoutee.profile.data.document.mongo.User getUser() {
        return user;
    }

    public void setUser(com.raxim.myscoutee.profile.data.document.mongo.User user) {
        this.user = user;
    }

    public List<Group> getGroups() {
        return groups;
    }

    public void setGroups(List<Group> groups) {
        this.groups = groups;
    }

    public List<Badge> getLikes() {
        return likes;
    }

    public void setLikes(List<Badge> likes) {
        this.likes = likes;
    }
}
