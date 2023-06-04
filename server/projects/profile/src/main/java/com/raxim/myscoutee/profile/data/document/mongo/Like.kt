package com.raxim.myscoutee.profile.data.document.mongo

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.DBRef
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.format.annotation.DateTimeFormat
import java.util.*

// single rate only
@Document(collection = "likes")
data class Like(

    @Id
    @field:JsonProperty(value = "id")
    val id: UUID? = UUID.randomUUID(),

    //double, single rate
    @field:JsonProperty(value = "double")
    val double: Boolean? = false,

    @DBRef
    @field:JsonProperty(value = "from")
    var from: Profile? = null,

    @DBRef
    @field:JsonProperty(value = "to")
    var to: Profile? = null,

    @field:JsonProperty(value = "rate")
    var rate: Int? = null,

    @DBRef
    @field:JsonProperty(value = "createdBy")
    var createdBy: Profile? = null,

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @field:JsonProperty(value = "createdDate")
    var createdDate: Date? = Date(),

    //in queries there are more rows, and it will be the average, taking care of the "double" flag also
    @field:JsonProperty(value = "distance")
    var distance: Long? = null, //20*(20-abs(rate1-rate2)) + profile difference

    // ref - like happened on idea, job, or promotion event
    @field:JsonIgnore
    var ref: UUID? = null
)