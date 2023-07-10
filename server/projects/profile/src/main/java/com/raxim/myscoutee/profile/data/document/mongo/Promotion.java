package com.raxim.myscoutee.profile.data.document.mongo;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

@Document(collection = "promotions")
public class Promotion {
    @Id
    @JsonProperty(value = "key")
    private UUID id;

    @JsonProperty(value = "name")
    private String name;

    // advertise date from, to
    @JsonProperty(value = "range")
    private RangeLocal range;

    @JsonIgnore
    private int numOfEvents;

    @DBRef
    @JsonIgnore
    private List<Event> events;

    @DBRef
    @JsonProperty(value = "group")
    private Group group;

    @JsonIgnore
    private Date createdDate;

    @JsonProperty(value = "createdBy")
    private UUID createdBy;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public RangeLocal getRange() {
        return range;
    }

    public void setRange(RangeLocal range) {
        this.range = range;
    }

    public int getNumOfEvents() {
        return numOfEvents;
    }

    public void setNumOfEvents(int cnt) {
        this.numOfEvents = cnt;
    }

    public List<Event> getEvents() {
        return events;
    }

    public void setEvents(List<Event> events) {
        this.events = events;
    }

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public UUID getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(UUID createdBy) {
        this.createdBy = createdBy;
    }

    // TODO: promotion sync
    public void sync() {

    }
}
