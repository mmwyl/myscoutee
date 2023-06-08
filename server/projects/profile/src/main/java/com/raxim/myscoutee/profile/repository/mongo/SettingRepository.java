package com.raxim.myscoutee.profile.repository.mongo;

import com.raxim.myscoutee.profile.data.document.mongo.Setting;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import java.util.UUID;

@RepositoryRestResource(
    collectionResourceRel = "settings",
    path = "settings"
)
public interface SettingRepository extends MongoRepository<Setting, UUID>,
    QuerydslPredicateExecutor<Setting> {

    @Query("{profile: ?0, key: ?1}")
    Setting findSettingByProfileAndKey(@Param("profileId") UUID profileId, @Param("key") String key);
}
