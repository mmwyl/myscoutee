package com.raxim.myscoutee.profile.repository.mongo;

import java.util.List;
import java.util.UUID;

import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import com.raxim.myscoutee.profile.data.document.mongo.Like;
import com.raxim.myscoutee.profile.data.document.mongo.LikeGroup;
import com.raxim.myscoutee.profile.data.dto.rest.LikeDTO;

@RepositoryRestResource(collectionResourceRel = "likes", path = "likes")
public interface LikeRepository extends MongoRepository<Like, UUID> {

    @Aggregation(pipeline = "findByParty")
    List<LikeGroup> findByParty(@Param("currUser") UUID currUser, @Param("likes") List<LikeDTO> likes);

    @Aggregation(pipeline = "findLikeGroupsByBatch")
    List<LikeGroup> findLikeGroupsByBatch(long lastIdx, long batchSize);

    @Aggregation(pipeline = "findLikeGroups")
    List<LikeGroup> findLikeGroups();

    @Aggregation(pipeline = "findLikeGroup")
    LikeGroup findLikeGroup(@Param("from") UUID from, @Param("to") UUID to, @Param("ref") UUID ref);
}
