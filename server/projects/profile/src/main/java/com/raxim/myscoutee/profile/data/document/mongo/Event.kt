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
@Document(collection = "events")
data class Event(
    @Id
    @field:JsonProperty(value = "key")
    val id: UUID? = UUID.randomUUID(),

    // the first event in the list
    //it contains the url of signal also
    //if first event is not added yet, it shows the form in the popup instead of the list
    //open dialog in list feature, if there is no element
    //routing hasFirst feature - if isFirst - than the popup *ngIf shows other elements - like signal url field

    //if you change dateFrom of general than every other item's date will slip
    @set:JsonIgnore
    @get:JsonProperty(value = "info")
    var info: EventItem? = null,

    @DBRef
    @field:JsonIgnore
    var items: MutableList<EventItem> = mutableListOf(),

    @DBRef
    @field:JsonIgnore
    val feedbacks: MutableList<Feedback> = mutableListOf(),

    @field:JsonIgnore
    var cnt: Int = 0,

    // should be filtered by group
    @field:JsonIgnore
    val group: UUID? = null,

    @field:JsonIgnore
    var position: GeoJsonPoint? = null,

    // cloned from
    @DBRef
    @field:JsonIgnore
    val ref: Event? = null,

    // Accepted (A) (by Organizer), Published/Promotion/Pending (P),
    // Inactive (I), Template (T), Under Review (U), Reviewed/Recommended (R), Rejected/Deleted (D)
    // Cancelled (C)
    // auto publish when general has been added
    // inactive means, just edited locally, before being published
    // when accepted by organizer, create chat room
    // accept form needs to have chat url
    @field:JsonProperty(value = "status")
    var status: String? = null,

    //idea, job, event for template
    @field:JsonIgnore
    val type: String? = null,

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @field:JsonProperty(value = "createdDate")
    var createdDate: Date? = Date(),

    @field:JsonProperty(value = "createdBy")
    var createdBy: UUID? = null,

    @JsonDeserialize(using = GeoJsonPointDeserializer::class)
    @set:JsonIgnore
    @get:JsonProperty(value = "positions")
    var positions: List<GeoJsonPoint> = mutableListOf()
)