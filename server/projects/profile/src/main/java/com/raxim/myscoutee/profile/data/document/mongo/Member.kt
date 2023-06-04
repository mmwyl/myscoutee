package com.raxim.myscoutee.profile.data.document.mongo

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import com.querydsl.core.annotations.QueryEntity
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.DBRef
import org.springframework.data.mongodb.core.mapping.Document
import java.util.*

// role for event
@QueryEntity
@Document(collection = "members")
data class Member(
    //profile id - only for the query
    @Id
    @field:JsonProperty(value = "key")
    val id: UUID? = UUID.randomUUID(),
    @DBRef
    @field:JsonProperty(value = "profile")
    val profile: Profile,

    // A (Accepted), R (rejected), I (invited), K (kicked)?? - maybe by more members than one, L (left) V (Verified)
    // T (Timed-Out), M (manager), P (promoter), LL (late leave), W (on waiting list)
    @field:JsonProperty(value = "status")
    var status: String? = null,

    //User (U), Admin (A), Promoter (P)
    @field:JsonProperty(value = "role")
    var role: String? = null,

    //JsonIgnore for field - authorization who can read it
    @set:JsonIgnore
    @get:JsonProperty(value = "code")
    var code: String? = null,

    @field:JsonIgnore
    var createdDate: Date? = Date(),

    //event item ref - members will be removed from event item
    @field:JsonIgnore
    var eventRef: UUID? = null
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other?.javaClass != javaClass) return false

        return this.id == (other as Member).id
    }

    override fun hashCode(): Int {
        var result = id?.hashCode() ?: 0
        result = 31 * result + profile.hashCode()
        result = 31 * result + (code?.hashCode() ?: 0)
        result = 31 * result + (createdDate?.hashCode() ?: 0)
        return result
    }
}