package com.raxim.myscoutee.profile.repository.mongo;

import com.raxim.myscoutee.profile.data.document.mongo.Badge;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import java.util.UUID;

@RepositoryRestResource(collectionResourceRel = "badges", path = "badges")
public interface BadgeRepository extends MongoRepository<Badge, UUID>, QuerydslPredicateExecutor<Badge> {
    // Additional custom methods can be defined here if needed
}
