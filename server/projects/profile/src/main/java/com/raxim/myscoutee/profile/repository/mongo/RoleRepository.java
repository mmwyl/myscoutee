package com.raxim.myscoutee.profile.repository.mongo;

import com.raxim.myscoutee.profile.data.document.mongo.Role;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

@Repository
public interface RoleRepository extends MongoRepository<Role, UUID> {

    @Query("{'profileId': ?0}")
    List<Role> findRoleByProfile(UUID profileId);
}
