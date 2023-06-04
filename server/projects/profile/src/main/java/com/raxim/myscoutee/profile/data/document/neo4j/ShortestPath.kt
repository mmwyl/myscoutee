package com.raxim.myscoutee.profile.data.document.neo4j

import org.springframework.data.neo4j.annotation.QueryResult

@QueryResult
class ShortestPath(
    val distance: Double? = null,
    val from: String? = null,
    val to: String? = null
)