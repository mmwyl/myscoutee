package com.raxim.myscoutee.profile.data.dto.rest;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.raxim.myscoutee.profile.data.document.mongo.User;

@JsonRootName("user")
public class UserDTO extends PageItemDTO {
    @JsonProperty(value = "user")
    private User user;

    public UserDTO(User user) {
        this.user = user;
    }

    public UserDTO() {
        // Default constructor
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
