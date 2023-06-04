package com.raxim.myscoutee.profile.data.document.neo4j

import org.neo4j.ogm.annotation.*

@RelationshipEntity(type = "MET")
data class MetEdge(
    @Id
    //@GeneratedValue
    val legacyId: Long? = null,
    @StartNode
    val from: ProfileNode? = null,
    @EndNode
    val to: ProfileNode? = null
)