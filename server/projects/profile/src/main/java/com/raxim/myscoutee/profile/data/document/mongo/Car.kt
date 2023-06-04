package com.raxim.myscoutee.profile.data.document.mongo

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import com.querydsl.core.annotations.QueryEntity
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.util.*

@QueryEntity
@Document(collection = "cars")
data class Car(
    @Id
    @field:JsonProperty(value = "key")
    val id: UUID? = UUID.randomUUID(),
    @field:JsonProperty(value = "regNum")
    val regNum: String? = null,
    @field:JsonProperty(value = "make")
    val make: String? = null,
    @field:JsonProperty(value = "model")
    val model: String? = null,
    @field:JsonProperty(value = "color")
    val color: String? = null,
    @field:JsonProperty(value = "year")
    val year: Int? = null,
    @field:JsonProperty(value = "capacity")
    val capacity: Int? = null,
    @field:JsonProperty(value = "images")
    val images: List<Image>? = emptyList(),

    @field:JsonIgnore
    var createdDate: Date? = Date(),

    //Active (A), Deleted (D)
    @field:JsonIgnore
    var status: String? = "A"
)