package com.raxim.myscoutee.profile.data.document.mongo

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.querydsl.core.annotations.QueryEntity
import com.raxim.myscoutee.common.data.converter.GeoJsonPointDeserializer
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.geo.GeoJsonPoint
import org.springframework.data.mongodb.core.index.GeoSpatialIndexType
import org.springframework.data.mongodb.core.index.GeoSpatialIndexed
import org.springframework.data.mongodb.core.mapping.DBRef
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.format.annotation.DateTimeFormat
import java.util.*

@QueryEntity
@Document(collection = "items")
data class EventItem(
    @Id
    @field:JsonProperty(value = "key")
    val id: UUID? = UUID.randomUUID(),
    //random, main -> only one (there is no random and main -> if all the places has been invited no calculation), general, car, accommodation, private (like phone number)
    //event is published if capacity has been set - auto capacity
    @field:JsonProperty(value = "type")
    val type: String? = null,
    @field:JsonProperty(value = "category")
    val category: String? = null,
    @field:JsonProperty(value = "name")
    val name: String? = null,
    @field:JsonProperty(value = "desc")
    val desc: String? = null,
    // google
    @field:JsonProperty(value = "urlRef")
    val urlRef: String? = null,
    //signal url
    @field:JsonProperty(value = "chatKey")
    val chatKey: String? = null,
    @field:JsonProperty(value = "range")
    var range: RangeLocal? = RangeLocal(),

    @field:JsonProperty(value = "capacity")
    var capacity: RangeInt? = RangeInt(),

    @field:JsonProperty(value = "telNum")
    val telNum: String? = null,
    @field:JsonProperty(value = "optional")
    val optional: Boolean? = null, // not mandatory event - not all participant needs to attend
    @field:JsonProperty(value = "amount")
    val amount: Amount? = Amount(),
    @field:JsonProperty(value = "ticket")
    val ticket: Boolean? = null,

    //@DBRef
    // kivalasztas belemasolas - modositas nem vezetodik at, ha valtozott a kocsi rendszama - update
    @field:JsonProperty(value = "car")
    val car: Car? = null,

    //the two item below is needed for the random calculation, which replaces members, if they are not available

    //when you met with a member
    @field:JsonProperty(value = "memberRange")
    var memberRange: RangeLocal? = RangeLocal(),

    @GeoSpatialIndexed(name = "position", type = GeoSpatialIndexType.GEO_2DSPHERE)
    @JsonDeserialize(using = GeoJsonPointDeserializer::class)
    @field:JsonProperty(value = "position")
    var position: GeoJsonPoint? = null,

    // opcionalis esemenyek - car is egy esemeny, amihez lehet csatlakozni
    // memberek telefonszama nincs itt mentve, egyelore
    @get:JsonIgnore
    @set:JsonProperty(value = "members")
    var members: MutableSet<Member>? = mutableSetOf(),

    @field:JsonProperty(value = "num")
    var num: Int = 0,

    @field:JsonProperty(value = "rule")
    var rule: Rule? = null,

    //Active (A), Deleted (D)
    @field:JsonProperty(value = "status")
    var status: String? = "A",

    @field:JsonProperty(value = "priority")
    val priority: Boolean? = null,

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @field:JsonProperty(value = "createdDate")
    var createdDate: Date? = Date()
)