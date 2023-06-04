package com.raxim.myscoutee.profile.data.document.neo4j

import org.neo4j.ogm.annotation.*

@RelationshipEntity(type = "LIKES")
data class LikeEdge(
    @Id
    //@GeneratedValue
    val legacyId: Long? = null,
    @Property
    val distance: Long? = null,
    @StartNode
    val from: ProfileNode? = null,
    @EndNode
    val to: ProfileNode? = null
)