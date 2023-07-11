package com.raxim.myscoutee.profile.data.document.mongo;

import java.time.LocalDateTime;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonProperty;

public class RangeLocal {
    @JsonProperty(value = "start")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime start;

    @JsonProperty(value = "end")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime end;

    public static RangeLocal of(LocalDateTime start, LocalDateTime end) {
        RangeLocal rangeLocal = new RangeLocal();
        rangeLocal.setStart(start);
        rangeLocal.setEnd(end);
        return rangeLocal;
    }

    public RangeLocal() {
        this(LocalDateTime.now(), LocalDateTime.now());
    }

    public RangeLocal(LocalDateTime start, LocalDateTime end) {
        this.start = start;
        this.end = end;
    }

    public LocalDateTime getStart() {
        return start;
    }

    public void setStart(LocalDateTime start) {
        this.start = start;
    }

    public LocalDateTime getEnd() {
        return end;
    }

    public void setEnd(LocalDateTime end) {
        this.end = end;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((start == null) ? 0 : start.hashCode());
        result = prime * result + ((end == null) ? 0 : end.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        RangeLocal other = (RangeLocal) obj;
        if (start == null) {
            if (other.start != null)
                return false;
        } else if (!start.equals(other.start))
            return false;
        if (end == null) {
            if (other.end != null)
                return false;
        } else if (!end.equals(other.end))
            return false;
        return true;
    }
}
