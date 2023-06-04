package com.raxim.myscoutee.profile.data.dto.rest

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty

data class Event(

    @field:JsonProperty(value = "event")
    val event: com.raxim.myscoutee.profile.data.document.mongo.Event? = null,

    @field:JsonProperty(value = "groupKey")
    var groupKey: Any? = null,

    @field:JsonProperty(value = "sortKey")
    val sortKey: Any? = null,

    @field:JsonIgnore
    val offset: List<Any>? = emptyList(),

    @field:JsonProperty(value = "rate")
    var rate: Int? = null,

    // group type
    @field:JsonProperty(value = "groupType")
    var groupType: String? = null,

    @field:JsonProperty(value = "promoType")
    var promoType: String? = null,

    @field:JsonProperty(value = "isPromotion")
    var isPromotion: Boolean? = null,

    @field:JsonProperty(value = "role")
    var role: String? = null
)