package com.raxim.myscoutee.profile.repository.mongo;

import com.raxim.myscoutee.profile.data.document.mongo.Schedule;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import java.util.Optional;
import java.util.UUID;

@RepositoryRestResource(
    collectionResourceRel = "schedules",
    path = "schedules"
)
public interface ScheduleRepository extends MongoRepository<Schedule, UUID>,
    QuerydslPredicateExecutor<Schedule> {

    @Query("{key: ?0}")
    Optional<Schedule> findByKey(String key);
}
