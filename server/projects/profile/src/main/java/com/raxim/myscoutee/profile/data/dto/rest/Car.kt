package com.raxim.myscoutee.profile.data.dto.rest

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty

data class Car(

    @field:JsonProperty(value = "car")
    val car: com.raxim.myscoutee.profile.data.document.mongo.Car? = null,

    @field:JsonIgnore
    val offset: List<Any>? = emptyList()
)