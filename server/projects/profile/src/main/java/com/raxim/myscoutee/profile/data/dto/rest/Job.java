package com.raxim.myscoutee.profile.data.dto.rest;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class Job {
    @JsonProperty(value = "job")
    private com.raxim.myscoutee.profile.data.document.mongo.Job job;

    @JsonIgnore
    private List<Object> offset;

    public com.raxim.myscoutee.profile.data.document.mongo.Job getJob() {
        return job;
    }

    public void setJob(com.raxim.myscoutee.profile.data.document.mongo.Job job) {
        this.job = job;
    }

    public List<Object> getOffset() {
        return offset;
    }

    public void setOffset(List<Object> offset) {
        this.offset = offset;
    }
}
