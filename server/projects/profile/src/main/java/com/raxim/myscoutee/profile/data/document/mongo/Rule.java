package com.raxim.myscoutee.profile.data.document.mongo;

import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonProperty;

//utolso x alkalommal akikkel talalkoztam szurje ki -> az esemeny ideje, es neve jelenjen meg felul (fieldset)
//random esemeny utan az egyezo nemuekre is lehet szavazni -> az esemeny koedukalt parameteru

//lista kepernyo ezekkel a szurokkel, hogy lehessen latni milyen sorrendben hivja meg az embereket
//az elso x ember a kapacitas alapjan mas szinu

//contact kepernyorol szures (sorba van rendezve kolcsonos rate-k alapjan)

@Document(collection = "rules")
public class Rule {
    //tehat az ismeroseim ismerosei egy melyseg, LikeRepository lekerdezes
    @JsonProperty(value = "met")
    private Boolean met;

    // don't invite less than 2-3 rated people for the event
    @JsonProperty(value = "rate")
    private Integer rate;

    //ehelyett utolso x esemeny
    //like updated time, nem az eventItem.memberRange helyett, nem range, hanem egy datum
    @JsonProperty(value = "range")
    private RangeLocal range;

    // Balanced Female/Male Ratio
    @JsonProperty(value = "balanced")
    private String balanced;

    public Boolean getMet() {
        return met;
    }

    public void setMet(Boolean met) {
        this.met = met;
    }

    // don't invite less than 2-3 rated people for the event
    public Integer getRate() {
        return rate;
    }

    public void setRate(Integer rate) {
        this.rate = rate;
    }

    public RangeLocal getRange() {
        return range;
    }

    public void setRange(RangeLocal range) {
        this.range = range;
    }

    // Balanced Female/Male Ratio
    public String getBalanced() {
        return balanced;
    }

    public void setBalanced(String balanced) {
        this.balanced = balanced;
    }
}
