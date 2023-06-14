package com.raxim.myscoutee.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.TestPropertySource;

import com.raxim.myscoutee.common.config.RepositoryConfig;
import com.raxim.myscoutee.common.repository.MongoDataLoaderTestExecutionListener;
import com.raxim.myscoutee.common.repository.TestData;
import com.raxim.myscoutee.profile.data.document.mongo.Like;
import com.raxim.myscoutee.profile.repository.mongo.LikeRepository;

@DataMongoTest
@Import({ RepositoryConfig.class })
@TestPropertySource(properties = { "de.flapdoodle.mongodb.embedded.version=6.0.6",
        "logging.level.org.springframework.data.mongodb=DEBUG" })
@TestData({ "mongo/profiles.json", "mongo/likes.json" })
@TestExecutionListeners(value = MongoDataLoaderTestExecutionListener.class, mergeMode = TestExecutionListeners.MergeMode.MERGE_WITH_DEFAULTS)
public class LikeRepositoryTest {

    @Autowired
    private LikeRepository likeRepository;

    @Test
    public void testShouldFindBothAll() {

        List<Like> likes = this.likeRepository.findBothAll("1900-01-01", 1.5);

        assertEquals(2, likes.size());

        Like like1 = likes.get(0);
        assertEquals("Evelyn", like1.getFrom().getFirstName());
        assertEquals("Liam", like1.getTo().getFirstName());

        Like like2 = likes.get(1);
        assertEquals("Oliver", like2.getFrom().getFirstName());
        assertEquals("Mia", like2.getTo().getFirstName());
    }
}
