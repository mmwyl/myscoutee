package com.raxim.myscoutee.profile.repository.mongo;

import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import com.raxim.myscoutee.profile.data.document.mongo.Sequence;

public class SequenceExtRepositoryImpl implements SequenceExtRepository {
    private final MongoTemplate mongoTemplate;

    public SequenceExtRepositoryImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public Sequence nextValue(String sequenceName) {
        Query query = new Query(Criteria.where("_id").is(sequenceName));
        Update update = new Update().inc("cnt", 1);

        FindAndModifyOptions options = new FindAndModifyOptions()
                .returnNew(true)
                .upsert(true);

        return mongoTemplate.findAndModify(query, update, options, Sequence.class);
    }

}
