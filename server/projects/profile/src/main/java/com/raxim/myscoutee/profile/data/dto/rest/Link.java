package com.raxim.myscoutee.profile.data.dto.rest;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Link {
    @JsonProperty(value = "link")
    private com.raxim.myscoutee.profile.data.document.mongo.Link link;

    @JsonProperty(value = "info")
    private LinkInfo info;

    public com.raxim.myscoutee.profile.data.document.mongo.Link getLink() {
        return link;
    }

    public void setLink(com.raxim.myscoutee.profile.data.document.mongo.Link link) {
        this.link = link;
    }

    public LinkInfo getInfo() {
        return info;
    }

    public void setInfo(LinkInfo info) {
        this.info = info;
    }
}
