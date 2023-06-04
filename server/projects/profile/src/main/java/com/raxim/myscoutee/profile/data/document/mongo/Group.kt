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
import java.util.*

@QueryEntity
@Document(collection = "groups")
data class Group(

    @Id
    @field:JsonProperty(value = "key")
    val id: UUID? = UUID.randomUUID(),

    @field:JsonProperty(value = "name")
    val name: String?,

    // dating/team building
    @field:JsonProperty(value = "type")
    val type: String?,

    // public, private
    @field:JsonProperty(value = "visibility")
    val visibility: String?,

    @field:JsonProperty(value = "images")
    val images: List<Image>? = emptyList(),

    @field:JsonProperty(value = "system")
    val system: Boolean? = false,

    @field:JsonProperty(value = "desc")
    val desc: String? = null,

    @GeoSpatialIndexed(name = "position", type = GeoSpatialIndexType.GEO_2DSPHERE)
    @JsonDeserialize(using = GeoJsonPointDeserializer::class)
    @field:JsonProperty(value = "position")
    var position: GeoJsonPoint? = null,

    @field:JsonIgnore
    var createdDate: Date? = Date(),

    //profileId
    @field:JsonIgnore
    val createdBy: UUID? = null
)