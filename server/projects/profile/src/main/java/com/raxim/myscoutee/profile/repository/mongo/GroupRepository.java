package com.raxim.myscoutee.profile.repository.mongo;

import java.util.List;
import java.util.UUID;

import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.query.Param;

import com.mongodb.client.model.geojson.Point;
import com.raxim.myscoutee.profile.data.document.mongo.Group;
import com.raxim.myscoutee.profile.data.dto.rest.GroupDTO;
import com.raxim.myscoutee.profile.data.dto.rest.PageParam;

public interface GroupRepository extends MongoRepository<Group, UUID> {

    @Query("{system: true}")
    List<Group> findSystemGroups();

    @Query("{type: ?0, system: true}")
    Group findSystemGroupByType(String type);

    @Aggregation(pipeline = "findAllGroups")
    List<GroupDTO> findAllGroups(PageParam pageParam,
            @Param("loc") Point loc,
            @Param("access") String access);

    @Aggregation(pipeline = "findGroupByProfile")
    List<GroupDTO> findGroupByProfile(
            @Param("type") String type,
            @Param("loc") Point loc,
            @Param("limit") int limit,
            @Param("step") int step,
            @Param("uuids") List<UUID> uuids,
            @Param("offset") Object[] offset);

}
