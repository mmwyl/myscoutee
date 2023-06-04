package com.raxim.myscoutee.profile.data.document.neo4j

import org.neo4j.ogm.annotation.*

//@QueryResult - customized return type for Repository
@NodeEntity("Profile")
data class ProfileNode(
        @Id
        //@GeneratedValue
        val legacyId: Long? = null,

        val uuid: String? = null,

        //@Relationship(type = "LIKES", direction = Relationship.UNDIRECTED)
        //val likes: LikeEdge? = null,

        //Man, Woman
        @Labels
        val labels: List<String>? = null
)