package com.raxim.myscoutee.profile.repository.mongo;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.raxim.myscoutee.profile.data.document.mongo.Sequence;

public interface SequenceRepository extends MongoRepository<Sequence, String>, SequenceExtRepository {
    public Sequence nextValue(String sequenceName);
}
