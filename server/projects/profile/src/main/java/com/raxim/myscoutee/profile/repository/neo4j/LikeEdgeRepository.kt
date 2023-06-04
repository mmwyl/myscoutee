package com.raxim.myscoutee.profile.repository.neo4j

import com.raxim.myscoutee.profile.data.document.neo4j.LikeEdge
import com.raxim.myscoutee.profile.data.document.neo4j.ShortestPath
import org.springframework.data.neo4j.annotation.Query
import org.springframework.data.neo4j.repository.Neo4jRepository
import org.springframework.stereotype.Repository

@Repository
interface LikeEdgeRepository : Neo4jRepository<LikeEdge,Long> {
    @Query(
        "CALL gds.alpha.allShortestPaths.stream({\n" +
                "  nodeProjection: 'Profile',\n" +
                "  relationshipProjection: {\n" +
                "    LIKES: {\n" +
                "      type: 'LIKES',\n" +
                "      properties: 'distance'\n" +
                "    }\n" +
                "  },\n" +
                "  relationshipWeightProperty: 'distance'\n" +
                "})\n" +
                "YIELD sourceNodeId, targetNodeId, distance\n" +
                "WITH sourceNodeId, targetNodeId, distance\n" +
                "WHERE gds.util.isFinite(distance) = true\n" +
                "\n" +
                "MATCH (source:Man) WHERE id(source) = sourceNodeId\n" +
                "MATCH (target:Woman) WHERE id(target) = targetNodeId\n" +
                "WITH source, target, distance\n" +
                "WHERE NOT (source)-[:MET]-(target)\n"+
                "\n" +
                "RETURN distance, source.uuid as from, target.uuid as to\n" +
                "ORDER BY distance ASC"
    )
    fun allShortestPath() : List<ShortestPath>
}