package com.raxim.myscoutee.profile.repository.neo4j

import com.raxim.myscoutee.profile.data.document.neo4j.MetEdge
import org.springframework.data.neo4j.repository.Neo4jRepository
import org.springframework.stereotype.Repository

@Repository
interface MetEdgeRepository : Neo4jRepository<MetEdge,Long> {
}