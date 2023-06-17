package com.raxim.myscoutee.profile.repository.mongo;

import com.raxim.myscoutee.profile.data.document.mongo.Sequence;

public interface SequenceExtRepository {
    public Sequence nextValue(String sequenceName);
}
