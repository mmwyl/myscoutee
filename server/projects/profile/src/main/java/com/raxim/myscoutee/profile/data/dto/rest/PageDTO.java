package com.raxim.myscoutee.profile.data.dto.rest;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

import java.util.List;

@JsonRootName("page")
public class PageDTO<T> {
    @JsonProperty(value = "values")
    private List<T> values;

    @JsonIgnore
    private List<Object> offset;

    @JsonProperty(value = "scroll")
    private Integer scroll;

    @JsonProperty(value = "step")
    private Object step;

    @JsonProperty(value = "role")
    private Object role;

    public PageDTO(List<T> values, List<Object> offset, Integer scroll, Object step) {
        this.values = values;
        this.offset = offset;
        this.scroll = scroll;
        this.step = step;
    }

    public PageDTO(List<T> values, List<Object> offset, Integer scroll) {
        this.values = values;
        this.offset = offset;
        this.scroll = scroll;
    }

    public PageDTO(List<T> values, List<Object> offset, Integer scroll, Object step, Object role) {
        this.values = values;
        this.offset = offset;
        this.scroll = scroll;
        this.step = step;
        this.role = role;
    }

    public PageDTO(List<T> values, List<Object> offset) {
        this.values = values;
        this.offset = offset;
    }

    public PageDTO() {
    }

    public List<T> getValues() {
        return values;
    }

    public void setValues(List<T> values) {
        this.values = values;
    }

    public List<Object> getOffset() {
        return offset;
    }

    public void setOffset(List<Object> offset) {
        this.offset = offset;
    }

    public Integer getScroll() {
        return scroll;
    }

    public void setScroll(Integer scroll) {
        this.scroll = scroll;
    }

    public Object getStep() {
        return step;
    }

    public void setStep(Object step) {
        this.step = step;
    }

    public Object getRole() {
        return role;
    }

    public void setRole(Object role) {
        this.role = role;
    }
}