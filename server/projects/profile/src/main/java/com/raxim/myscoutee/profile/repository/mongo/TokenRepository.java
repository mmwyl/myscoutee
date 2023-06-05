package com.raxim.myscoutee.profile.repository.mongo;

import com.raxim.myscoutee.profile.data.document.mongo.Token;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;
import java.util.UUID;

@RepositoryRestResource(collectionResourceRel = "tokens", path = "tokens")
public interface TokenRepository extends MongoRepository<Token, UUID>, QuerydslPredicateExecutor<Token> {

    @Query("{deviceKey: ?0}")
    Token findByDeviceId(String deviceId);

    @Query("{uuid: ?0}")
    List<Token> findByUserIds(UUID[] uuids);
}
