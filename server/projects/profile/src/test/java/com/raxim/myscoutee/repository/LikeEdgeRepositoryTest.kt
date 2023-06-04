package com.raxim.myscoutee.profile.repository

import com.raxim.myscoutee.profile.repository.neo4j.LikeEdgeRepository
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.data.neo4j.DataNeo4jTest
import org.springframework.test.context.junit.jupiter.SpringExtension

@Disabled
@ExtendWith(SpringExtension::class)
@DataNeo4jTest
class LikeEdgeRepositoryTest {

    @Autowired
    lateinit var likeEdgeRepository: LikeEdgeRepository

    @Test
    @Throws(Exception::class)
    fun generateEvent() {

        //val likes = likeEdgeRepository.findAll()
        val likes = likeEdgeRepository.allShortestPath()

        assert(likes.count() > 0)
    }
}