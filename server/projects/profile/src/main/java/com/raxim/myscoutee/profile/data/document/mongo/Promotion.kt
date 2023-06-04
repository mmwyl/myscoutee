package com.raxim.myscoutee.profile.data.document.mongo

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import com.querydsl.core.annotations.QueryEntity
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.DBRef
import org.springframework.data.mongodb.core.mapping.Document
import java.util.*

@QueryEntity
@Document(collection = "promotions")
data class Promotion(
    @Id
    @field:JsonProperty(value = "key")
    val id: UUID? = UUID.randomUUID(),

    @field:JsonProperty(value = "name")
    val name: String? = null,

    // job, idea, event
    @field:JsonProperty(value = "type")
    val type: String? = null,

    // advertise date from, to
    @field:JsonProperty(value = "range")
    var range: RangeLocal? = RangeLocal(),

    // select template
    //@DBRef
    @field:JsonProperty(value = "item")
    val item: Event? = null,

    @field:JsonIgnore
    var cnt: Int = 0,

    @DBRef
    @field:JsonIgnore
    var events: MutableList<Event> = mutableListOf(),

    // accordion item by slot
    @field:JsonProperty(value = "slots")
    val slots: MutableSet<Slot> = mutableSetOf(),

    @field:JsonProperty(value = "groupType")
    val groupType: String? = null,

    @DBRef
    @field:JsonProperty(value = "group")
    val group: Group? = null,

    @field:JsonIgnore
    var createdDate: Date? = Date(),

    @field:JsonProperty(value = "createdBy")
    val createdBy: UUID? = null
)