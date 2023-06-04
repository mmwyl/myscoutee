package com.raxim.myscoutee.profile.repository.neo4j

import com.raxim.myscoutee.profile.data.document.neo4j.LikeEdge
import com.raxim.myscoutee.profile.data.document.neo4j.ProfileNode
import org.springframework.data.neo4j.annotation.Query
import org.springframework.data.neo4j.repository.Neo4jRepository
import org.springframework.stereotype.Repository

@Repository
interface ProfileNodeRepository : Neo4jRepository<ProfileNode, Long>