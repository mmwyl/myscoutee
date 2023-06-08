package com.raxim.myscoutee.profile.repository.mongo;

import com.raxim.myscoutee.profile.data.document.mongo.Message;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MessageRepository extends MongoRepository<Message, UUID> {

    @Query("{lang: ?0}")
    Optional<Message> findByLang(String lang);

    // Add any additional methods or custom queries if needed
}
