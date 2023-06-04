package com.raxim.myscoutee.profile.data.dto.rest

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.raxim.myscoutee.common.data.converter.GeoJsonPointDeserializer
import org.springframework.data.mongodb.core.geo.GeoJsonPoint
import org.springframework.data.mongodb.core.index.GeoSpatialIndexType
import org.springframework.data.mongodb.core.index.GeoSpatialIndexed

data class EventItem(

    @field:JsonProperty(value = "item")
    val item: com.raxim.myscoutee.profile.data.document.mongo.EventItem? = null,

    @field:JsonProperty(value = "groupKey")
    val groupKey: Any? = null,

    @field:JsonProperty(value = "sortKey")
    val sortKey: Any? = null,

    @field:JsonIgnore
    val offset: List<Any>? = emptyList(),

    @field:JsonProperty(value = "main")
    val main: Boolean? = false,

    @field:JsonProperty(value = "category")
    val category: String? = null,

    @JsonDeserialize(using = GeoJsonPointDeserializer::class)
    @field:JsonProperty(value = "positions")
    var positions: List<GeoJsonPoint>? = null,

    @field:JsonProperty(value = "isPromotion")
    var isPromotion: Boolean? = null,

    @field:JsonProperty(value = "isMember")
    var isMember: Boolean? = null,

    @field:JsonProperty(value = "role")
    var role: String? = null
)