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
import java.time.LocalDateTime
import java.util.*


@QueryEntity
@Document(collection = "profiles")
data class Profile(
    @Id
    @field:JsonProperty(value = "key")
    val id: UUID? = UUID.randomUUID(),
    @field:JsonProperty(value = "firstName")
    val firstName: String? = null,
    @field:JsonProperty(value = "gender")
    val gender: String? = null,
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @field:JsonProperty(value = "birthday")
    val birthday: Date? = null,
    @field:JsonProperty(value = "smoker")
    val smoker: Boolean? = false,
    @field:JsonProperty(value = "marital")
    val marital: String? = null,
    @field:JsonProperty(value = "hasChild")
    val hasChild: Boolean? = false,
    @field:JsonProperty(value = "height")
    val height: Int? = null,
    @field:JsonProperty(value = "physique")
    val physique: String? = null,
    @field:JsonProperty(value = "languages")
    val languages: List<String>? = emptyList(),
    @field:JsonProperty(value = "religion")
    val religion: String? = null,
    @field:JsonProperty(value = "profession")
    val profession: String? = null,
    @field:JsonProperty(value = "images")
    val images: List<Image>? = emptyList(),

    @field:JsonProperty(value = "voice")
    val voice: String? = null,

    @field:JsonProperty(value = "desc")
    val desc: String? = null,
    @field:JsonProperty(value = "website")
    val website: String? = null,

    @DBRef
    @field:JsonIgnore
    var cars: MutableList<Car>? = mutableListOf(),

    @DBRef
    @field:JsonIgnore
    val schools: MutableList<School>? = mutableListOf(),
    // https://blog.alessandrorosa.com/2017/geojson-serialization-spring-mongodb/
    @GeoSpatialIndexed(name = "position", type = GeoSpatialIndexType.GEO_2DSPHERE)
    @JsonDeserialize(using = GeoJsonPointDeserializer::class)
    @get:JsonIgnore
    @set:JsonProperty(value = "position")
    var position: GeoJsonPoint? = null,
    @field:JsonProperty(value = "status")
    var status: String? = "I", //P (pending), F (friends only), I (invisible), A (All), S (suspended), D (deleted - group leave)

    @field:JsonIgnore
    var createdDate: Date? = Date(),

    @field:JsonIgnore
    val group: UUID? = null,

    @field:JsonIgnore
    val score: Int = 0,

    @field:JsonIgnore
    var lastLogin: LocalDateTime? = null

)