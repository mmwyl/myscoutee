package com.raxim.myscoutee.profile.data.dto.rest;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class Car {
    @JsonProperty(value = "car")
    private com.raxim.myscoutee.profile.data.document.mongo.Car car;
    
    @JsonIgnore
    private List<Object> offset;

    public com.raxim.myscoutee.profile.data.document.mongo.Car getCar() {
        return car;
    }

    public void setCar(com.raxim.myscoutee.profile.data.document.mongo.Car car) {
        this.car = car;
    }

    public List<Object> getOffset() {
        return offset;
    }

    public void setOffset(List<Object> offset) {
        this.offset = offset;
    }
}
